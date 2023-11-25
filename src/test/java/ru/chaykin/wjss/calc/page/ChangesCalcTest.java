package ru.chaykin.wjss.calc.page;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.chaykin.wjss.change.ChangeType;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;

import static ru.chaykin.wjss.calc.page.LocalPageFactory.createLocalPage;
import static ru.chaykin.wjss.calc.page.PageFetcherFactory.createLocalPageFetcher;
import static ru.chaykin.wjss.calc.page.PageFetcherFactory.remotePageFetcher;
import static ru.chaykin.wjss.calc.page.RemotePageFactory.createRemotePage;
import static ru.chaykin.wjss.change.ChangeType.*;

class ChangesCalcTest {

    @Test
    void emptyTest() {
	Context context = Context.createBuilder(null, null)
			.localPageFetcher(createLocalPageFetcher())
			.remotePageFetcher(remotePageFetcher())
			.build();

	var changes = new PageChangesCalc(context).calculateChanges();
	Assertions.assertTrue(changes.isEmpty());
    }

    @Test
    void remotesOnlyTest() {
	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1"),
			createRemotePage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(createLocalPageFetcher())
			.remotePageFetcher(remotePageFetcher)
			.build();

	var changes = toMap(new PageChangesCalc(context).calculateChanges());
	Assertions.assertEquals(2, changes.size());
	Assertions.assertEquals(REMOTE_NEW, changes.get(1L).getChange());
	Assertions.assertEquals(REMOTE_NEW, changes.get(2L).getChange());
    }

    @Test
    void localsOnlyTest() {
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", new Date(), "PAGE_1"),
			createLocalPage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher())
			.build();

	var changes = toMap(new PageChangesCalc(context).calculateChanges());
	Assertions.assertEquals(2, changes.size());
	Assertions.assertEquals(REMOTE_DELETED, changes.get(1L).getChange());
	Assertions.assertEquals(REMOTE_DELETED, changes.get(2L).getChange());
    }

    @Test
    void noChangesTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2"));

	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1"),
			createRemotePage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher)
			.build();

	var changes = new PageChangesCalc(context).calculateChanges();
	Assertions.assertTrue(changes.isEmpty());
    }

    @Test
    void movePageTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2"));

	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1"),
			createRemotePage(2L, "moved/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher)
			.build();

	var changes = toMap(new PageChangesCalc(context).calculateChanges());
	Assertions.assertEquals(1, changes.size());
	Assertions.assertEquals(REMOTE_UPDATED, changes.get(2L).getChange());
    }

    @Test
    void noConflictsTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2 (UP)", "PAGE_2"),
			createLocalPage(3L, "common/p3", createdAt, "PAGE_3"),
			createLocalPage(4L, "common/p4", createdAt, null, "PAGE_4"));

	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1"),
			createRemotePage(2L, "common/p2", new Date(), "PAGE_2"),
			createRemotePage(3L, "common/p3", new Date(), "PAGE_3 (UP)"),
			createRemotePage(4L, "common/p4", new Date(), "PAGE_4"),
			createRemotePage(5L, "common/p5", new Date(), "PAGE_5"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher)
			.build();

	var changes = toMap(new PageChangesCalc(context).calculateChanges());
	Assertions.assertEquals(4, changes.size());

	Assertions.assertEquals(LOCAL_UPDATED, changes.get(2L).getChange());
	Assertions.assertEquals(REMOTE_UPDATED, changes.get(3L).getChange());
	Assertions.assertEquals(LOCAL_DELETED, changes.get(4L).getChange());
	Assertions.assertEquals(REMOTE_NEW, changes.get(5L).getChange());
    }

    @Test
    void conflictsTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1 (L_UP)", "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2 (L_UP)", "PAGE_2"),
			createLocalPage(3L, "common/p3", createdAt, null, "PAGE_3"),
			createLocalPage(4L, "common/p4", createdAt, null, "PAGE_4"));

	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1 (S_UP)"),
			createRemotePage(3L, "common/p3", new Date(), "PAGE_3 (S_UP)"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher)
			.build();

	var changes = toMap(new PageChangesCalc(context).calculateChanges());
	Assertions.assertEquals(4, changes.size());

	assertChange(changes.get(1L), LOCAL_UPDATED, REMOTE_UPDATED);
	assertChange(changes.get(2L), LOCAL_UPDATED, REMOTE_DELETED);
	assertChange(changes.get(3L), LOCAL_DELETED, REMOTE_UPDATED);
	assertChange(changes.get(4L), LOCAL_DELETED, REMOTE_DELETED);
    }

    private Map<Long, PageChange> toMap(Collection<PageChange> changes) {
	return changes.stream().collect(Collectors.toMap(c -> c.getResource().getId(), Function.identity()));
    }

    private void assertChange(PageChange change, ChangeType expectedLocalType, ChangeType expectedRemoteType) {
	Assertions.assertEquals(expectedLocalType, change.getLocalChange());
	Assertions.assertEquals(expectedRemoteType, change.getRemoteChange());
    }
}
