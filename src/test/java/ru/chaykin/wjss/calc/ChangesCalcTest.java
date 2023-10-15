package ru.chaykin.wjss.calc;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.graphql.query.PageListQuery.PageListItem;
import ru.chaykin.wjss.utils.PageHashUtils;

import static org.mockito.Mockito.*;
import static ru.chaykin.wjss.calc.ChangeType.*;
import static ru.chaykin.wjss.calc.PageFetcherFactory.createLocalPageFetcher;
import static ru.chaykin.wjss.calc.PageFetcherFactory.remotePageFetcher;

class ChangesCalcTest {

    @Test
    void emptyTest() {
	LocalPageFetcher localPageFetcher = mock();
	when(localPageFetcher.fetch()).thenReturn(Collections.emptyMap());

	RemotePageFetcher remotePageFetcher = mock();
	when(remotePageFetcher.fetch()).thenReturn(Collections.emptyMap());

	var changes = new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges();
	Assertions.assertTrue(changes.isEmpty());
    }

    @Test
    void remotesOnlyTest() {
	LocalPageFetcher localPageFetcher = createLocalPageFetcher();

	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(1L, "common/p1", new Date(), "PAGE_1"),
			createRemotePage(2L, "common/p2", new Date(), "PAGE_2"));

	var changes = toMap(new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges());
	Assertions.assertEquals(2, changes.size());

	assertChange(changes.get(1L), REMOTE_NEW);
	assertChange(changes.get(2L), REMOTE_NEW);
    }

    @Test
    void localsOnlyTest() {
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", new Date(), "PAGE_1"),
			createLocalPage(2L, "common/p2", new Date(), "PAGE_2"));

	RemotePageFetcher remotePageFetcher = remotePageFetcher();

	var changes = toMap(new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges());
	Assertions.assertEquals(2, changes.size());

	assertChange(changes.get(1L), REMOTE_DELETED);
	assertChange(changes.get(2L), REMOTE_DELETED);
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

	var changes = new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges();
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

	var changes = toMap(new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges());
	Assertions.assertEquals(1, changes.size());

	assertChange(changes.get(2L), REMOTE_UPDATED);
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

	var changes = toMap(new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges());
	Assertions.assertEquals(4, changes.size());

	assertChange(changes.get(2L), LOCAL_UPDATED);
	assertChange(changes.get(3L), REMOTE_UPDATED);
	assertChange(changes.get(4L), LOCAL_DELETED);
	assertChange(changes.get(5L), REMOTE_NEW);
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

	var changes = toMap(new ChangesCalc(localPageFetcher, remotePageFetcher).calculateChanges());
	Assertions.assertEquals(4, changes.size());

	assertChange(changes.get(1L), LOCAL_UPDATED, REMOTE_UPDATED);
	assertChange(changes.get(2L), LOCAL_UPDATED, REMOTE_DELETED);
	assertChange(changes.get(3L), LOCAL_DELETED, REMOTE_UPDATED);
	assertChange(changes.get(4L), LOCAL_DELETED, REMOTE_DELETED);
    }

    private RemotePage createRemotePage(long id, String path, Date updatedAt, String content) {
	PageListItem pli = new PageListItem(id, path, "ru", "testTitle", "testDescr", "markdown", updatedAt,
			Collections.emptyList());

	RemotePage page = spy(new RemotePage(null, pli));
	doReturn(content).when(page).getContent();
	return page;
    }

    private LocalPage createLocalPage(long id, String path, Date updatedAt, String content) {
	return createLocalPage(id, path, updatedAt, content, content);
    }

    private LocalPage createLocalPage(long id, String path, Date updatedAt, String content, String oldContent) {
	LocalPage page = mock();
	when(page.getId()).thenReturn(id);
	when(page.getTitle()).thenReturn("testTitle");
	when(page.description()).thenReturn("testDescr");
	when(page.getLocale()).thenReturn("ru");
	when(page.getRemotePath()).thenReturn(path);
	when(page.getLocalPath()).thenReturn(Path.of("."));
	when(page.getContentType()).thenReturn("markdown");
	when(page.getRemoteUpdatedAt()).thenReturn(updatedAt.getTime());
	when(page.getTags()).thenReturn(Collections.emptyList());

	doReturn(content).when(page).getContent();

	String hash = PageHashUtils.md5PageHash(page);
	if (!Objects.equals(content, oldContent)) {
	    hash = createLocalPage(id, path, updatedAt, oldContent).getMd5Hash();
	}

	when(page.getMd5Hash()).thenReturn(hash);

	return page;
    }

    private Map<Long, PageChange> toMap(Collection<PageChange> changes) {
	return changes.stream().collect(Collectors.toMap(c -> c.getPage().getId(), Function.identity()));
    }

    private void assertChange(PageChange change, ChangeType... expectedTypes) {
	Assertions.assertEquals(Set.of(expectedTypes), change.getChanges());
    }
}
