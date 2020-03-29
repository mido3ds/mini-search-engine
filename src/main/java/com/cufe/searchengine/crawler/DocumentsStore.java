package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);
	private final BlockingQueue<Document> store = new LinkedBlockingQueue<>();

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Value("${crawler.maxDocuments}")
	private int maxDocuments;
	private long docsCount = 0;

	@EventListener
	private void handleDBInitialized(DBInitializedEvent event) {
		Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents;", Integer.class);
		if (size == null) {
			throw new IllegalStateException("`documents` count shouldn't be null");
		}
		docsCount = size;

		// commit thread
		new Thread(() -> {
			log.info("started");

			while (!Thread.currentThread().isInterrupted()) {
				try {
					Document document = store.take();
					int rows = 0;

					try {
						rows = document.store(this.jdbcTemplate);
					} catch (CannotGetJdbcConnectionException e) {
						continue;
					}

					if (rows != 1) {
						log.error("couldn't insert document with url=" + document.getUrl());
						Thread.currentThread().interrupt();
					}
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			}

			throw new IllegalStateException("thread stopped, can't continue storing documents");
		}, "commit").start();
	}

	public void add(String url, String doc) throws InterruptedException {
		store.put(new Document(doc, url, System.currentTimeMillis()));
		docsCount++;

		if (isFull()) {
			publisher.publishEvent(new CrawlingFinishedEvent(this));
		}
	}

	private boolean isFull() {
		return docsCount >= maxDocuments;
	}
}