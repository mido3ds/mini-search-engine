package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.util.DocumentFilterer;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);
	private final AtomicLong docsCount = new AtomicLong();
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Value("${crawler.maxDocuments}")
	private int maxDocuments;

	@EventListener
	private void onDBInitialized(DBInitializer.DBInitializedEvent event) {
		Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents;", Integer.class);
		if (size == null) {
			throw new IllegalStateException("`documents` count shouldn't be null");
		}
		docsCount.set(size);
	}

	private void storeToDB(Document document) throws Exception {
		int rows = document.store(this.jdbcTemplate);
		if (rows != 1) {
			log.error("couldn't insert document with url=" + document.getUrl());
		}
	}

	public void add(String url, String doc) {
		if (!StringUtils.isHtml(doc)) {
			log.info("doc at url {} is probably not html, ignore it", url);
			return;
		}

		Document document = new Document(DocumentFilterer.textFromHtml(doc), url, System.currentTimeMillis());

		try {
			storeToDB(document);
		} catch (Exception e) {
			log.error(e.getMessage());
			return;
		}

		docsCount.getAndIncrement();

		if (isFull()) {
			publisher.publishEvent(new CrawlingFinishedEvent(this));
		}
	}

	private boolean isFull() {
		return docsCount.getAndUpdate(l -> l >= maxDocuments ? 0 : l) >= maxDocuments;
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
