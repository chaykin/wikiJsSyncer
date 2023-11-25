package ru.chaykin.wjss.link;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;

@Log4j2
@RequiredArgsConstructor
public class LinkManager {
    private static final Pattern LINK_REGEXP = Pattern.compile("(\\[.+?])\\(/.+?/(.+?)\\)");
    private static final Pattern UNLINK_REGEXP = Pattern.compile("(\\[.+?])\\(file\\://(.+?)\\)");

    private final Context context;

    private Map<String, IPage> remotePages;
    private Map<Path, IPage> localPages;

    public String wrapByLocalLinks(String content) {
	StringBuilder result = new StringBuilder();
	Matcher matcher = LINK_REGEXP.matcher(content);
	while (matcher.find()) {
	    matcher.appendReplacement(result, Matcher.quoteReplacement(wrapLink(matcher)));
	}
	matcher.appendTail(result);

	return result.toString();
    }

    public String unwrapLocalLinks(String content) {
	StringBuilder result = new StringBuilder();
	Matcher matcher = UNLINK_REGEXP.matcher(content);
	while (matcher.find()) {
	    matcher.appendReplacement(result, Matcher.quoteReplacement(unwrapLink(matcher)));
	}
	matcher.appendTail(result);

	return result.toString();
    }

    private String wrapLink(Matcher matcher) {
	String name = matcher.group(1);
	String[] remoteFullPath = matcher.group(2).split("#");
	String remoteBasePath = remoteFullPath[0];
	String section = remoteFullPath.length > 1 ? ("#" + remoteFullPath[1]) : "";

	IPage page = getRemotePageByPath(remoteBasePath);
	if (page == null) {
	    log.debug("There is no page with path {}. Skip this link resolution", remoteBasePath);
	    return matcher.group();
	}

	Path localPath = page.getLocalPath().toAbsolutePath();
	String link = String.format("%s(file://%s%s)", name, localPath, section);
	log.debug("Replace link: {} -> {}", matcher.group(), link);

	return link;
    }

    private String unwrapLink(Matcher matcher) {
	String name = matcher.group(1);
	String[] localFullPath = matcher.group(2).split("#");
	Path localPath = Path.of(localFullPath[0]);
	String section = localFullPath.length > 1 ? ("#" + localFullPath[1]) : "";

	IPage page = getLocalPageByPath(localPath);
	if (page == null) {
	    log.debug("There is no page with path {}. Skip this link resolution", localPath);
	    return matcher.group();
	}

	String locale = page.getLocale();
	String remotePath = page.getRemotePath();

	log.debug("Replace link: {} -> {}/{}", localPath, locale, remotePath);
	return String.format("%s(/%s/%s%s)", name, locale, remotePath, section);
    }

    private IPage getRemotePageByPath(String remotePath) {
	if (remotePages == null) {
	    remotePages = context.remotePages().values().stream()
			    .collect(Collectors.toMap(IPage::getRemotePath, Function.identity()));
	}

	return remotePages.get(remotePath);
    }

    private IPage getLocalPageByPath(Path localPath) {
	if (localPages == null) {
	    localPages = context.localPages().values().stream()
			    .collect(Collectors.toMap(p -> p.getLocalPath().toAbsolutePath(), Function.identity()));
	}

	return localPages.get(localPath);
    }
}
