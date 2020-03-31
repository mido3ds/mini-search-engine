package com.cufe.searchengine.util;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpHtmlPattern {
	private static final Pattern HTTP_URL_PATTERN = Pattern.compile("(https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{2," +
		"256}\\.[a-z]{2,4}\\b)([-a-zA-Z0-9@:%_+" +
		".~#?&//=]*)");
	private static final Pattern NON_HTML_URL_PATTERN = Pattern.compile("(https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,"
		+ "256}\\.[a-z]{2,4}\\b)([-a-zA-Z0-9@:%_+" + ".~#?&//=]*)\\." + "(css" + "|pdf|xml|js" + "|jpg|png|gif|json)");
	private static final Pattern HTML_TITLE = Pattern.compile("<title>(.+)</title>",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

	public static String extractWebsite(String url) {
		// TODO: <BUG> ro.wikipedia.org and en.wikipedia.org must be both wikipedia.org
		Matcher matcher = HTTP_URL_PATTERN.matcher(url);
		return matcher.matches() ? matcher.group(1) : "";
	}

	public static String[] extractURLs(String html) {
		return HTTP_URL_PATTERN.matcher(html)
			.results()
			.map(MatchResult::group)
			.map(String::trim)
			.distinct()
			.toArray(String[]::new);
	}

	public static String extractHtmlTitle(String html) {
		List<String> collect = HTML_TITLE.matcher(html).results().map(s -> s.group(1)).collect(Collectors.toList());
		return collect.size() > 0 ? collect.get(0): "";
	}

	public static boolean couldBeHtml(String url) {
		return !NON_HTML_URL_PATTERN.matcher(url).matches();
	}
}
