package com.cufe.searchengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class Indexer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Indexer.class);

	/**
	 * jdbcTemplate: access sqlite db, shared between all the server classes.
	 * schema is in `src/main/resources/sqlite_schema.sql`
	 * and any initial data is in `src/main/resources/populate_db.sql`
	 */
	private JdbcTemplate jdbcTemplate;

	public Indexer(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

		if (jdbcTemplate == null) {
			throw new IllegalArgumentException("jdbcTemplate is null");
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
