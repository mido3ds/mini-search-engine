package com.cufe.searchengine.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class Indexer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Indexer.class);

	private final int sleepTimeMillis;
	private final JdbcTemplate jdbcTemplate;

	public Indexer(int sleepTimeMillis, JdbcTemplate jdbcTemplate) {
		this.sleepTimeMillis = sleepTimeMillis;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run() {
		log.info("started");

		while (true) {
			try {
				Thread.sleep(sleepTimeMillis);
			} catch (InterruptedException ignored) {
			}

			log.info("restarted");

			// TODO
		}
	}
}
