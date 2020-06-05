package com.cufe.searchengine.ranker;

import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.KeywordsTable;
import com.cufe.searchengine.server.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.lang.Math;
import java.text.*;

@Component
public class RelevanceRanker {
	private static final Logger log = LoggerFactory.getLogger(RelevanceRanker.class);

	@Autowired
	private DocumentsTable documentsTable;
	@Autowired
	private KeywordsTable keywordsTable;

	public ArrayList<QueryResult> rank(ArrayList<QueryResult> queryResults, List<String> keywords, String clientAlpha3) throws Exception {
        log.info("started query results ranking");

		List<Float> ranks = new ArrayList<Float>();
		List<Float> tfidfs = new ArrayList<Float>();
		List<String> dates = new ArrayList<String>();

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
			float tfidf = 0;
			for(int j=0; j<keywords.size(); j++) {
				float tf = (float)keywordsTable.selectWordCount(url, keywords.get(j)) / documentsTable.selectWordCount(url);
				tfidf += (tf * idfs.get(j));
			}
			tfidfs.add(tfidf);
			ranks.add(documentsTable.selectURLRank(url));
			dates.add(documentsTable.selectURLPubDate(url));
		}

		log.info("computing individual scores");
		List<Integer> rankScores = computeScores(ranks);
		List<Integer> tfidfScores = computeScores(tfidfs);
		List<Integer> dateScores = computeDateScores(dates);

		log.info("computing overall scores");
		List<Integer> overallScores = new ArrayList<Integer>();
		for(int i=0; i<rankScores.size(); i++) {
			overallScores.add(rankScores.get(i) + tfidfScores.get(i) + dateScores.get(i));
		}

		log.info("sorting query results");
		ArrayList<QueryResult> results = sortResults(queryResults, overallScores);

		if (clientAlpha3.isEmpty()) {
			return results;
		}

		return sortByCountry(results, clientAlpha3);
	}

	private ArrayList<QueryResult> sortResults(ArrayList<QueryResult> queryResults, List<Integer> scores) {
		ArrayList<QueryResult> sortedQueryResults = new ArrayList<QueryResult>();
		int size = queryResults.size();
		for(int i=0; i<size; i++) {
			int maxIdx = getMaxIndex(scores);
			sortedQueryResults.add(queryResults.get(maxIdx));
			queryResults.remove(maxIdx);
			scores.remove(maxIdx);
		}
		return sortedQueryResults;
	}

	private ArrayList<QueryResult> sortByCountry(ArrayList<QueryResult> queryResults, String clientAlpha3) throws Exception {
		ArrayList<QueryResult> countryResults = new ArrayList<QueryResult>();
		ArrayList<QueryResult> otherResults = new ArrayList<QueryResult>();
		for(QueryResult result : queryResults) {
			String url = result.getLink();
			String countryCode = documentsTable.selectURLCountryCode(url);
			if (countryCode.equals(clientAlpha3)) {
				countryResults.add(result);
			} else {
				otherResults.add(result);
			}
		}
		countryResults.addAll(otherResults);
		return countryResults;
	}

	private List<Integer> computeScores(List<Float> list) {
		int size = list.size(); 
		List<Integer> scores = new ArrayList<Integer>(Collections.nCopies(list.size(), 0));
		for(int i=0; i<size; i++) {
			int minIdx = getMinIndex(list);
			scores.set(minIdx, i);
			list.remove(minIdx);
		}
		return scores;
	}

	private List<Integer> computeDateScores(List<String> list) {
		List<Date> dates = new ArrayList<Date>();
		for(String date : list) {
			try {
				if (date.isEmpty()) {
					dates.add(stringToDate("0000-00-00"));
				} else {
					dates.add(stringToDate(date));
				}
			} catch (ParseException e) {
				log.error("date parsing failed");
			}
		}
		int size = dates.size(); 
		List<Integer> scores = new ArrayList<Integer>(Collections.nCopies(list.size(), 0));
		for(int i=0; i<size; i++) {
			int minIdx = getMinDateIndex(dates);
			scores.set(minIdx, i);
			list.remove(minIdx);
		}
		return scores;
	}

	private Integer getMinIndex(List<Float> list) {
		Float minVal = Collections.min(list);
		return list.indexOf(minVal);
	}

	private Integer getMaxIndex(List<Integer> list) {
		int maxVal = Collections.max(list);
		return list.indexOf(maxVal);
	}

	private Date stringToDate(String date) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");;
		return formatter.parse(date);
	}

	private Integer getMinDateIndex(List<Date> list) {
		Date minVal = Collections.min(list);
		return list.indexOf(minVal);
	}
}
