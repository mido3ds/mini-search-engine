package com.cufe.searchengine.indexer;

import com.cufe.searchengine.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Indexer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Indexer.class);

	@Value("${indexer.waitTimeMillis}")
	private int sleepTimeMillis;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void run() {
		log.info("started");

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(sleepTimeMillis);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			log.info("restarted");

			// TODO
		}
	}

	@Component
	private static class IndexerRunner {
		private final Logger log = LoggerFactory.getLogger(IndexerRunner.class);

		@Autowired
		private Indexer indexer;

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("received DBInitializedEvent, starting Indexer");

			new Thread(indexer, "indexer").start();
		}
	}
}
