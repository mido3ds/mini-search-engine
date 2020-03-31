package com.cufe.searchengine.query;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.server.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PhraseProcessor {
	private static final Logger log = LoggerFactory.getLogger(PhraseProcessor.class);
	private static final Pattern QUOTES = Pattern.compile("(?:'(.+)')|(?:\"(.+)\")");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<QueryResult> search(List<String> phrases) {
		Optional<String> reducedLikes = phrases.stream()
			.map(String::toLowerCase)
			.distinct()
			.map(p -> MessageFormat.format("content LIKE \"%{0}%\"", p))
			.reduce((s, s2) -> MessageFormat.format("{0} OR {1}", s, s2));

		if (!reducedLikes.isPresent()) {
			return new ArrayList<>();
		}

		String query = "SELECT content, url FROM documents WHERE " + reducedLikes.get() + ";";

//		log.info("query {}", query);

		return jdbcTemplate
			.query(query, (row, i) -> new Document(row.getString(1), row.getString(2), 0))
			.stream()
			.map(d -> new QueryResult().title(d.getTitle()).snippet(d.getSnippet(phrases)).link(d.getUrl()))
			.collect(Collectors.toList());
	}

	public List<QueryResult> search(String query) {
		return search(QUOTES.matcher(query).results().map(MatchResult::group).collect(Collectors.toList()));
	}

	public String removePhrases(String query) {
		return QUOTES.matcher(query).replaceAll("");
	}
}
