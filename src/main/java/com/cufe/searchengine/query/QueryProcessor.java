package com.cufe.searchengine.query;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.KeywordsTable;
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
	private KeywordsTable keywordsTable;
	@Autowired
	private PhraseProcessor phraseProcessor;
	@Autowired
	private RelevanceRanker relevanceRanker;
	@Autowired
	private TrendsHandler trendsHandler;

	/**
	 * @return all search results, ranked
	 */
	public List<QueryResult> search(String query, String ipAddress, boolean isImage) {
		log.info("received query = {}", query);

		log.info("processing query");
		ArrayList<QueryResult> queryResults = new ArrayList<>(phraseProcessor.search(query, isImage));

		query = phraseProcessor.removePhrases(query);

		List<String> keywords = DocumentFilterer.keywordsFromQuery(query);

		log.info("getting client geographic location");
		String clientAlpha3 = "";
		try {
			clientAlpha3 = GeoUtils.countryAlpha3FromAlpha2(GeoUtils.countryAlpha2FromIP(GeoUtils.getPublicIPAddr(ipAddress)));	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("failed to get client country");
		}

		log.info("updating country trends");
		try {
			trendsHandler.updateTrends(query, clientAlpha3);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("trends update failed");
		}

		log.info("preparing query results");
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
			documents = queryDocuments(keywords, isImage);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("returning empty results");
			return new ArrayList<>();
		}

		queryResults.addAll(documents.stream()
			.map(document -> new QueryResult().title(document.getTitle())
				.link(document.getUrl())
				.snippet(isImage? null : document.getSnippet(keywords)))
			.collect(Collectors.toList()));

		try {
			return relevanceRanker.rank(queryResults, keywords, clientAlpha3);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("query ranking failed");
			return queryResults;
		}
	}

	private List<Document> queryDocuments(List<String> keywords, boolean isImage) throws Exception {
		return documentsTable.selectContentUrlSorted(keywords, isImage);
	}

	/**
	 * @param query uncompleted query
	 * @return list of suggestions to appear before hitting enter in the search bar
	 */
	public List<String> suggest(String query) {
		try {
			return keywordsTable.selectWordStartWith(query);
		} catch(Exception e) {
			e.printStackTrace();
			log.error("suggestion loading failed");
			return new ArrayList<>();
		}
	}
}
