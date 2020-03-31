package com.cufe.searchengine.query;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.server.model.QueryResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Ranker {
	public List<QueryResult> sort(
		ArrayList<QueryResult> queryResults, List<Document> documents, List<String> keywords
	) {
		return queryResults; // TODO
	}
}
