package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Value("${crawler.maxDocuments}")
	private int maxDocuments;
	private long docsCount = 0;

	@EventListener
	private void onDBInitialized(DBInitializer.DBInitializedEvent event) {
		Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents;", Integer.class);
		if (size == null) {
			throw new IllegalStateException("`documents` count shouldn't be null");
		}
		docsCount = size;
	}

	private void storeToDB(Document document) throws InterruptedException {
		int rows = 0;

		while (true) {
			try {
				rows = document.store(this.jdbcTemplate);
			} catch (CannotGetJdbcConnectionException e) {
				Thread.sleep(100);
				continue;
			}

			break;
		}

		if (rows != 1) {
			log.error("couldn't insert document with url=" + document.getUrl());
			Thread.currentThread().interrupt();
		}
	}

	public void add(String url, String doc) throws InterruptedException {
		if (!StringUtils.isHtml(doc)) {
			log.info("doc at url {} is probably not html, ignore it", url);
			return;
		}

		Document document = new Document(doc, url, System.currentTimeMillis());

		storeToDB(document);
		docsCount++;

		if (isFull()) {
			publisher.publishEvent(new CrawlingFinishedEvent(this));
		}
	}

	private boolean isFull() {
		return docsCount >= maxDocuments;
	}

	public static class CrawlingFinishedEvent extends ApplicationEvent {
		/**
		 * Create a new ApplicationEvent.
		 *
		 * @param source the object on which the event initially occurred (never {@code null})
		 */
		public CrawlingFinishedEvent(Object source) {
			super(source);
		}
	}
}
