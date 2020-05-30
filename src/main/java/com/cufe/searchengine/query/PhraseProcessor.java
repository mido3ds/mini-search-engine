package com.cufe.searchengine.query;

import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.server.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PhraseProcessor {
	private static final Logger log = LoggerFactory.getLogger(PhraseProcessor.class);
	private static final Pattern QUOTES = Pattern.compile("(?:'(.+)')|(?:\"(.+)\")");

	@Autowired
	private DocumentsTable documentsTable;

	private List<QueryResult> search(List<String> phrases, boolean isImage) {
		try {
			return documentsTable.selectContentUrlLikePhrases(phrases, isImage);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("returning empty results");

			return new ArrayList<>();
		}
	}

	public List<QueryResult> search(String query, boolean isImage) {
		return search(QUOTES.matcher(query).results().map(MatchResult::group).collect(Collectors.toList()), isImage);
	}

	public String removePhrases(String query) {
		return QUOTES.matcher(query).replaceAll("");
	}
}
