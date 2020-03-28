package com.cufe.searchengine.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpPattern {
	private static final Pattern HTTP_URL_PATTERN = Pattern.compile("(https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b)([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");

	public static String extractWebsite(String url) {
		// TODO: <BUG> ro.wikipedia.org and en.wikipedia.org must be both wikipedia.org
		Matcher matcher = HTTP_URL_PATTERN.matcher(url);
		return matcher.matches() ? matcher.group(1) : "";
	}

	public static String[] extractURLs(String html) {
		return HTTP_URL_PATTERN
			.matcher(html)
			.results()
			.map(MatchResult::group)
			.toArray(String[]::new);
	}
}
