package ru.chaykin.wjss.change;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.chaykin.wjss.change.fetch.LocalPageFetcher;
import ru.chaykin.wjss.change.fetch.ServerPageFetcher;
import ru.chaykin.wjss.context.Context;

import static ru.chaykin.wjss.change.ChangeType.*;
import static ru.chaykin.wjss.change.LocalPageFactory.createLocalPage;
import static ru.chaykin.wjss.change.PageFetcherFactory.createLocalPageFetcher;
import static ru.chaykin.wjss.change.PageFetcherFactory.serverPageFetcher;
import static ru.chaykin.wjss.change.ServerPageFactory.createServerPage;

class IncomingChangesResolverTest {

    @Test
    void emptyTest() {
	Context context = Context.createBuilder(null, null)
			.localPageFetcher(createLocalPageFetcher())
			.serverPageFetcher(serverPageFetcher())
			.build();

	Assertions.assertTrue(resolveChanges(context).isEmpty());
    }

    @Test
    void serversOnlyTest() {
	ServerPageFetcher serverPageFetcher = serverPageFetcher(
			createServerPage(1L, "common/p1", new Date(), "PAGE_1"),
			createServerPage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(createLocalPageFetcher())
			.serverPageFetcher(serverPageFetcher)
			.build();

	var changes = resolveChanges(context);
	Assertions.assertEquals(2, changes.size());
	Assertions.assertEquals(NEW, changes.get(1L));
	Assertions.assertEquals(NEW, changes.get(2L));
    }

    @Test
    void localsOnlyTest() {
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", new Date(), "PAGE_1"),
			createLocalPage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.serverPageFetcher(serverPageFetcher())
			.build();

	var changes = resolveChanges(context);
	Assertions.assertEquals(2, changes.size());
	Assertions.assertEquals(DELETED, changes.get(1L));
	Assertions.assertEquals(DELETED, changes.get(2L));
    }

    @Test
    void noChangesTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2"));

	ServerPageFetcher serverPageFetcher = serverPageFetcher(
			createServerPage(1L, "common/p1", new Date(), "PAGE_1"),
			createServerPage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.serverPageFetcher(serverPageFetcher)
			.build();

	Assertions.assertTrue(resolveChanges(context).isEmpty());
    }

    @Test
    void movePageTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2"));

	ServerPageFetcher serverPageFetcher = serverPageFetcher(
			createServerPage(1L, "common/p1", new Date(), "PAGE_1"),
			createServerPage(2L, "moved/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.serverPageFetcher(serverPageFetcher)
			.build();

	var changes = resolveChanges(context);
	Assertions.assertEquals(1, changes.size());
	Assertions.assertEquals(UPDATED, changes.get(2L));
    }

    @Test
    void multipleChangesTest() {
	Date createdAt = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(1L, "common/p1", createdAt, "PAGE_1"),
			createLocalPage(2L, "common/p2", createdAt, "PAGE_2"),
			createLocalPage(3L, "common/p3", createdAt, "PAGE_3"),
			createLocalPage(4L, "common/p4", createdAt, "PAGE_4"));

	ServerPageFetcher serverPageFetcher = serverPageFetcher(
			createServerPage(1L, "common/p1", new Date(), "PAGE_1"),
			createServerPage(2L, "common/p2", new Date(), "PAGE_2 (UP)"),
			createServerPage(3L, "common/p3", new Date(), "PAGE_3 (UP)"),
			createServerPage(5L, "common/p5", new Date(), "PAGE_5"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.serverPageFetcher(serverPageFetcher)
			.build();

	var changes = resolveChanges(context);
	Assertions.assertEquals(4, changes.size());

	Assertions.assertEquals(UPDATED, changes.get(2L));
	Assertions.assertEquals(UPDATED, changes.get(3L));
	Assertions.assertEquals(DELETED, changes.get(4L));
	Assertions.assertEquals(NEW, changes.get(5L));
    }

    private Map<Long, ChangeType> resolveChanges(Context context) {
	return new IncomingChangesResolver().resolveChanges(context.localPages(), context.serverPages());
    }
}
