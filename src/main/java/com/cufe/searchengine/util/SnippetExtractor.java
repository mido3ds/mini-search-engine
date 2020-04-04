package com.cufe.searchengine.util;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnippetExtractor {
	public static final String DOTS = "...";
	private static final int MAX_SIZE = 164;

	public static String extract(String text, List<String> keywords) {
		text = text.trim();
		if (text.length() == 0 || keywords.size() == 0) {
			return text;
		}

		// pattern from keywords
		Pattern pattern = Pattern.compile(
			keywords.stream()
				.map(SnippetExtractor::normalizeString)
				.distinct()
				.reduce((a, b) -> a + "|" + b).orElse(""),
			Pattern.CASE_INSENSITIVE
		);

		// get words that match keywords
		List<Match> matches = words(text)
			.filter(w -> pattern.matcher(w.text).matches())
			.collect(Collectors.toList());

		// emphasize keywords in text
		text = replacementPattern(matches)
			.matcher(text)
			.replaceAll(matchResult -> "<em>" + matchResult.group() + "</em>");

		return cut(text, matches);
	}

	private static Pattern replacementPattern(List<Match> matches) {
		return Pattern.compile(matches.stream().map(m -> m.original).collect(Collectors.joining("|")),
			Pattern.CASE_INSENSITIVE);
	}

	private static String cut(String text, List<Match> matches) {
		if (matches.size() == 0) {
			return text;
		}

		Match first = matches.get(0);
		Match last = matches.get(matches.size() - 1);

		if (getSize(first, last) < MAX_SIZE) {
			last.end = Math.min(MAX_SIZE - getSize(first, last), text.length());
		}

		if (getSize(first, last) > MAX_SIZE) {
			last.end = Math.min(first.start + MAX_SIZE, text.length());
		}

		String cutText = text.substring(first.start, last.end);

		if (first.start != 0) {
			cutText = DOTS + cutText;
		}

		if (last.end != text.length()) {
			cutText += DOTS;
		}

		return cutText;
	}

	private static int getSize(Match first, Match last) {
		return last.end - first.start;
	}

	private static Stream<Match> words(String text) {
		return Pattern.compile("\\w+").matcher(text)
			.results()
			.map(s -> new Match(normalizeString(s.group()), s.start(), s.end()));
	}

	private static String normalizeString(String s) {
		return Stemmer.stem(s).trim();
	}

	private static class Match {
		String text;
		String original;
		int start, end;

		public Match(String text, int start, int end) {
			this.text = text;
			this.start = start;
			this.end = end;
			this.original = text;
		}
	}
}
