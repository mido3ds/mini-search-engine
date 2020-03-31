package com.cufe.searchengine.query;

import com.cufe.searchengine.server.model.QueryResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PhraseProcessor {
	private static final Pattern QUOTES = Pattern.compile("(?:'(.+)')|(?:\"(.+)\")");

	private List<QueryResult> search(List<String> collect) {
		return new ArrayList<>(); // TODO
	}

	public List<QueryResult> search(String query) {
		return search(QUOTES.matcher(query).results().map(MatchResult::group).collect(Collectors.toList()));
	}

	public String removePhrases(String query) {
		return QUOTES.matcher(query).replaceAll("");
	}
}
