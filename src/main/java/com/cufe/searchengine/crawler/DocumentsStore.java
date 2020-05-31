package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DocumentsStore implements Runnable {
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

	private final BlockingQueue<Document> queue = new LinkedBlockingQueue<>();

	private void storeToDB(Document document) throws Exception {
		documentsTable.replace(document.getUrl(), document.getContent(), document.getTimeMillis(), 
								document.getCounter(), document.getPubDate(), document.getCountryCode(), document.isImage());
	}

	public void add(String url, String content, String pubDate, boolean isImage) throws InterruptedException {
		String countryCode = "";
		try {
			countryCode = GeoUtils.countryAlpha3FromAlpha2(GeoUtils.countryAlpha2FromIP(GeoUtils.ipFromURL(url)));
		} catch (Exception e) {
			log.error("failed to get country code, error: " + e.getMessage());
		}

		Document document = new Document(
			content, url, System.currentTimeMillis(), 0, 1, pubDate, countryCode
		).counter(urlsStore.getCounter()).isImage(isImage);

		queue.put(document);
	}

	private boolean isFull() {
		return docsCount.getAndUpdate(l -> l >= maxDocuments ? 0 : l) >= maxDocuments;
	}

	@Override
	public void run() {
		log.info("started");

		Integer size;
		try {
			size = documentsTable.size();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("closing");
			return;
		}
		if (size == null) {
			throw new IllegalStateException("`documents` count shouldn't be null");
		}
		docsCount.set(size);

		while (!Thread.interrupted()) {
			Document document = null;
			try {
				document = queue.take();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			try {
				storeToDB(Objects.requireNonNull(document));
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

		log.error("interrupted");
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

	@Component
	private static class DocumentsStoreRunner {
		private static final Logger log = LoggerFactory.getLogger(DocumentsStoreRunner.class);

		@Autowired
		private DocumentsStore documentsStore;

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("received DBInitializedEvent");

			new Thread(documentsStore, "docstore").start();
		}
	}
}
