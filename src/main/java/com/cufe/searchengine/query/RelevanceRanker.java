package com.cufe.searchengine.query;

import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.KeywordsTable;
import com.cufe.searchengine.server.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.lang.Math;

@Component
public class RelevanceRanker {
	private static final Logger log = LoggerFactory.getLogger(RelevanceRanker.class);

	@Autowired
	private DocumentsTable documentsTable;
	@Autowired
	private KeywordsTable keywordsTable;

	public ArrayList<QueryResult> rank(ArrayList<QueryResult> queryResults, List<String> keywords) throws Exception {
        log.info("started query results ranking");

		List<Float> ranks = new ArrayList<Float>();
		List<Float> idfs = new ArrayList<Float>();
		int docNum = documentsTable.size();

		log.info("calculating TF-IDF");
		for(String word : keywords) {
			int docWithWordNum = keywordsTable.selectCountbyWord(word);
			float idf = (float)Math.log((float)docNum/docWithWordNum);
			idfs.add(idf);
		}

		for(int i=0; i<queryResults.size(); i++) {
			String url = queryResults.get(i).getLink();
			float rank = 0;
			for(int j=0; j<keywords.size(); j++) {
				float tf = (float)keywordsTable.selectWordCount(url, keywords.get(j)) / documentsTable.selectWordCount(url);
				rank += (tf * idfs.get(j));
			}
			rank += documentsTable.selectURLRank(url);
			ranks.add(rank);
		}

		log.info("sorting query results");
		return sortResults(queryResults, ranks);
	}

	private ArrayList<QueryResult> sortResults(ArrayList<QueryResult> queryResults, List<Float> ranks) {
		ArrayList<QueryResult> sortedQueryResults = new ArrayList<>();
		Integer size = queryResults.size();
		for(int i=0; i<size; i++) {
			Integer maxIdx = getMaxIndex(ranks);
			sortedQueryResults.add(queryResults.get(maxIdx));
			queryResults.remove(maxIdx);
			ranks.remove(maxIdx);
		}
		return sortedQueryResults;
	}

	private Integer getMaxIndex(List<Float> list) {
		Float maxVal = Collections.max(list);
		return list.indexOf(maxVal);
	}
}
