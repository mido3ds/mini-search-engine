package com.cufe.searchengine.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);
	private final BlockingQueue<Document> store = new LinkedBlockingQueue<>();

	private JdbcTemplate jdbcTemplate;
	private boolean full = false;

	@Autowired
	public DocumentsStore(final JdbcTemplate jdbcTemplate,
						  @Value("${crawler.maxDocuments}") final int maxDocuments,
						  @Value("${crawler.documentsSizeWaitTime}") final int documentsSizeWaitTime) {
		this.jdbcTemplate = jdbcTemplate;

		// commit thread
		new Thread(() -> {
			log.info("started");

			while (!Thread.currentThread().isInterrupted()) {
				try {
					Document document = store.take();
					if (document.store(this.jdbcTemplate) != 1) {
						log.error("couldn't insert docuemnt with url=" + document.getUrl());
						Thread.currentThread().interrupt();
					}
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			}

			throw new IllegalStateException("thread stopped, can't continue storing documents");
		}).start();

		// isFull thread
		new Thread(() -> {
			// wait until db is created
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ignored) {
			}

			while (!Thread.currentThread().isInterrupted() && !full) {
				try {
					Thread.sleep(documentsSizeWaitTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents;", Integer.class);
				if (size == null) {
					throw new IllegalStateException("`documents` count shouldn't be null");
				}

				full = size >= maxDocuments;
				if (full) {
					log.info("full");
				}
			}
		}).start();
	}

	public void add(String url, String doc) throws InterruptedException {
		store.put(new Document(doc, url, System.currentTimeMillis()));
	}

	public boolean isFull() {
		return full;
	}
}
