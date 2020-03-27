package com.cufe.searchengine.query;

import com.cufe.searchengine.server.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryProcessor {
	/**
	 * jdbcTemplate: access sqlite db, shared between all the server classes.
	 * schema is in `src/main/resources/sqlite_schema.sql`
	 * and any initial data is in `src/main/resources/populate_db.sql`
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @return all search results
	 */
	public List<QueryResult> search(String query) {
		// TODO
		ArrayList<QueryResult> queryResults = new ArrayList<>();

		for (int i = 0; i < 97; i++) {
			queryResults.add(
				new QueryResult()
					.title("Wikipedia")
					.link("https://www.wikipedia.org")
					.snippet("Wikipedia is a free online encyclopedia, " +
						"created and edited by volunteers around the world and hosted " +
						"by the Wikimedia Foundation.")
			);
		}

		return queryResults;
	}

	/**
	 * @param query uncomplete query
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
