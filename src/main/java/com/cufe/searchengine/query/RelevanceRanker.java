package com.cufe.searchengine.query;

import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.server.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RelevanceRanker {
	private static final Logger log = LoggerFactory.getLogger(RelevanceRanker.class);

	public static ArrayList<QueryResult> rank(ArrayList<QueryResult> queryResults) {
        // TODO: implement relevance rank (TF-IDF)
		return queryResults;
	}
}
