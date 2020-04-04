package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.util.DocumentFilterer;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class DocumentsStore {
	private final Logger log = LoggerFactory.getLogger(DocumentsStore.class);
	private final AtomicLong docsCount = new AtomicLong();
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private DocumentsTable documentsTable;
	@Value("${crawler.maxDocuments}")
	private int maxDocuments;
	@Autowired
	private UrlsStore urlsStore;

	@EventListener
	private void onDBInitialized(DBInitializer.DBInitializedEvent event) throws Exception {
		Integer size = documentsTable.size();
		if (size == null) {
			throw new IllegalStateException("`documents` count shouldn't be null");
		}
		docsCount.set(size);
	}

	private void storeToDB(Document document) throws Exception {
		documentsTable.replace(document.getUrl(), document.getContent(), document.getTimeMillis(), document.getCounter());
	}

	public void add(String url, String doc) {
		if (!StringUtils.isHtml(doc)) {
			log.info("doc at url {} is probably not html, ignore it", url);
			return;
		}

		Document document = new Document(
			DocumentFilterer.textFromHtml(doc), url, System.currentTimeMillis()
		).counter(urlsStore.getCounter());

		try {
			storeToDB(document);
		} catch (Exception e) {
			log.error(e.getMessage());
			return;
		}

		docsCount.getAndIncrement();
		publisher.publishEvent(new DocumentStoredEvent(this, document.getUrl(), document.getTimeMillis()));

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

	public static class DocumentStoredEvent extends ApplicationEvent {
		private final String url;
		private final long timeMillis;

		public DocumentStoredEvent(Object source, String url, long timeMillis) {
			super(source);
			this.url = url;
			this.timeMillis = timeMillis;
		}

		public long getTimeMillis() {
			return timeMillis;
		}

		public String getUrl() {
			return url;
		}
	}
}
