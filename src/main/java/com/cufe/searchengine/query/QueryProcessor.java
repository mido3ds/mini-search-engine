package com.cufe.searchengine.query;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.server.model.QueryResult;
import com.cufe.searchengine.util.DocumentFilterer;
import com.cufe.searchengine.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryProcessor {
	private static final Logger log = LoggerFactory.getLogger(QueryProcessor.class);

	@Autowired
	private DocumentsTable documentsTable;
	@Autowired
	private PhraseProcessor phraseProcessor;
	@Autowired
	private RelevanceRanker relevanceRanker;

	/**
	 * @return all search results, ranked
	 */
	public List<QueryResult> search(String query, String ipAddress) {
		//		log.info("received query = {}", query);

		ArrayList<QueryResult> queryResults = new ArrayList<>(phraseProcessor.search(query));

		//		log.info("queryResults from phraseProcessor .size() = {}", queryResults.size());

		query = phraseProcessor.removePhrases(query);

		//		log.info("query after removing phrases = {}", query);

		List<String> keywords = DocumentFilterer.keywordsFromQuery(query);

		//		log.info("extracted keywords = {}", keywords);

		String clientAlpha3 = new String("");
		try {
			clientAlpha3 = GeoUtils.countryAlpha3FromAlpha2(GeoUtils.countryAlpha2FromIP(GeoUtils.getPublicIPAddr(ipAddress)));	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("failed to get client country");
		}

		if (keywords.size() == 0) {
			try {
				return relevanceRanker.rank(queryResults, keywords, clientAlpha3);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("query ranking failed");
				return queryResults;
			}
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

		try {
			return relevanceRanker.rank(queryResults, keywords, clientAlpha3);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("query ranking failed");
			return queryResults;
		}
	}

	private List<Document> queryDocuments(List<String> keywords) throws Exception {
		return documentsTable.selectContentUrlSorted(keywords);
	}

	/**
	 * @param query uncompleted query
	 * @return list of suggestions to appear before hitting enter in the search bar
	 */
	public List<String> suggest(String query) {
		// TODO: autocomplete
		ArrayList<String> strings = new ArrayList<>();
		strings.add("Did you mean this?");
		strings.add("no, you probably meant this");
		strings.add("no?");
		strings.add("then what did you mean? i can't figure out");

		return strings;
	}
}
