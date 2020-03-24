package com.cufe.searchengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Crawler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Crawler.class);

	/**
	 * jdbcTemplate: access sqlite db, shared between all the server classes.
	 * schema is in `src/main/resources/sqlite_schema.sql`
	 * and any initial data is in `src/main/resources/populate_db.sql`
	 */
	private JdbcTemplate jdbcTemplate;

	/**
	 * seedSet: list of http/s strings following the format "http[s]://.../"
	 * when one crawler finishes fetching on link, it should remove it from the list
	 * note: no two crawlers should start from the same link
	 * note: seeds are stored in `src/main/resources/crawler_seed`
	 */
	private List<String> seedSet;

	public Crawler(JdbcTemplate jdbcTemplate, List<String> seedSet) {
		this.jdbcTemplate = jdbcTemplate;
		this.seedSet = seedSet;
	}

	public static void createThreads(int numThreads, JdbcTemplate jdbcTemplate, List<String> seedSet) {
		if (numThreads <= 0) {
			throw new IllegalArgumentException("numThreads must be positive");
		}
		if (jdbcTemplate == null) {
			throw new IllegalArgumentException("jdbcTemplate is null");
		}
		if (seedSet.size() == 0) {
			throw new IllegalArgumentException("empty seed set");
		}

		log.info("creating " + numThreads + " of threads of crawler, with seedSet of size " + seedSet.size());

		for (int i = 0; i < numThreads; i++) {
			new Thread(new Crawler(jdbcTemplate, seedSet), String.valueOf(i)).start();
		}
	}

	@Override
	public void run() {
		log.info("started");

//		while (true) {
		// TODO
//		}

		log.info("ended");
	}
}
