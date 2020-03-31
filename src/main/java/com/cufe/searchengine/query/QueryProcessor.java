package com.cufe.searchengine.query;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.server.model.QueryResult;
import com.cufe.searchengine.util.DBUtils;
import com.cufe.searchengine.util.DocumentFilterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryProcessor {
	private static final Logger log = LoggerFactory.getLogger(QueryProcessor.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private PhraseProcessor phraseProcessor;
	@Autowired
	private Ranker ranker;

	/**
	 * @return all search results, ranked
	 */
	public List<QueryResult> search(String query) {
		//		log.info("received query = {}", query);

		ArrayList<QueryResult> queryResults = new ArrayList<>(phraseProcessor.search(query));

		//		log.info("queryResults from phraseProcessor .size() = {}", queryResults.size());

		query = phraseProcessor.removePhrases(query);

		//		log.info("query after removing phrases = {}", query);

		List<String> keywords = DocumentFilterer.keywordsFromQuery(query);

		//		log.info("extracted keywords = {}", keywords);

		if (keywords.size() == 0) {
			return queryResults;
		}

		List<Document> documents;
		try {
			documents = queryDocuments(keywords);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("returning empty results");
			return new ArrayList<>();
		}

		//		log.info("queried documents size = {}", documents.size());

		queryResults.addAll(documents.stream()
			.map(document -> new QueryResult().title(document.getTitle())
				.link(document.getUrl())
				.snippet(document.getSnippet(keywords)))
			.collect(Collectors.toList()));

		return ranker.sort(queryResults, documents, keywords);
	}

	private List<Document> queryDocuments(List<String> keywords) throws Exception {
		StringBuilder builder = new StringBuilder("SELECT content, url FROM documents d " + "INNER JOIN " +
			"keywords_documents kd ON d.ROWID = kd.docID " + "INNER JOIN " + "keywords k ON k.ROWID = kd.wordID AND k.word in (");
		for (int i = 0; i < keywords.size(); i++) {
			builder.append("?");
			if (i != keywords.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(");");

		return DBUtils.waitLock(100, () -> jdbcTemplate.query(builder.toString(),
			(row, i) -> new Document(row.getString(1), row
				.getString(2), 0), keywords.toArray()));
	}

	/**
	 * @param query uncompleted query
	 * @return list of suggestions to appear before hitting enter in the search bar
	 */
	public List<String> suggest(String query) {
		// TODO
		ArrayList<String> strings = new ArrayList<>();
		strings.add("Did you mean this?");
		strings.add("no, you probably meant this");
		strings.add("no?");
		strings.add("then what did you mean? i can't figure out");

		return strings;
	}
}
