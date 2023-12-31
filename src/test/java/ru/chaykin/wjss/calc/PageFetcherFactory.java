package ru.chaykin.wjss.calc;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageFetcherFactory {

    public static LocalPageFetcher createLocalPageFetcher(LocalPage... pages) {
	var pageMap = toMap(pages);

	LocalPageFetcher fetcher = mock();
	when(fetcher.fetch(any())).thenReturn(pageMap);

	return fetcher;
    }

    public static RemotePageFetcher remotePageFetcher(RemotePage... pages) {
	var pageMap = toMap(pages);

	RemotePageFetcher fetcher = mock();
	when(fetcher.fetch()).thenReturn(pageMap);

	return fetcher;
    }

    private static <T extends IPage> Map<Long, T> toMap(T... pages) {
	return Stream.of(pages).collect(Collectors.toMap(IPage::getId, Function.identity()));
    }
}
