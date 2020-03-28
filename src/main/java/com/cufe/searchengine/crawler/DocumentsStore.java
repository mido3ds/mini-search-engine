package com.cufe.searchengine.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);
	private final BlockingQueue<Document> store = new LinkedBlockingQueue<>();

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public DocumentsStore(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

		new Thread(() -> {
			log.info("started");
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Document document = store.take();
					document.store(this.jdbcTemplate);
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}

			throw new IllegalStateException("thread stopped, can't continue storing documents");
		}).start();
	}

	public void add(String url, String doc) throws InterruptedException {
		store.put(new Document(doc, url, System.currentTimeMillis()));
	}

	public String get(String url) {
		// TODO
		return "";
	}

}
