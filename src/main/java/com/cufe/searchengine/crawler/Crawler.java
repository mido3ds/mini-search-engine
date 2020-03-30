package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.util.HttpPattern;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

// TODO: when crawling finishes, restart again from the beginning (from the seed)

@Component
public class Crawler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Crawler.class);

	@Value("${crawler.userAgent}")
	private String userAgent;
	@Autowired
	private UrlsStore urlsStore;
	@Autowired
	private DocumentsStore documentsStore;

	@Override
	public void run() {
		log.info("started");

		while (!Thread.currentThread().isInterrupted()) {
			String url = null;
			try {
				url = urlsStore.poll();
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			String document;

			try {
				document = loadURL(url);
			} catch (Exception e) {
				log.error("url=" + url + ",error=" + e.getMessage());
				continue;
			}

			if (document.length() == 0) {
				continue;
			}

			String[] urls = HttpPattern.extractURLs(document);
			log.info("extracted " + urls.length + " urls from " + url + ", document.length=" + document.length());
			try {
				for (String u : urls) {
					urlsStore.add(u);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			try {
				documentsStore.add(url, document);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		log.info("ended");
	}

	private String loadURL(String link) throws IOException {
		InputStream inputStream = null;
		String result = "";

		URL url = new URL(link);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("User-Agent", userAgent);

		try {
			inputStream = urlConnection.getInputStream();
			result = StringUtils.streamToString(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return result;
	}

	@Component
	private static class CrawlersRunner {
		private static final Logger log = LoggerFactory.getLogger(CrawlersRunner.class);

		@Value("${crawler.numThreads}")
		private int numThreads;
		@Autowired
		private Crawler crawler;
		private List<Thread> threads = new ArrayList<>();

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("creating " + numThreads + " of threads of crawler");

			for (int i = 0; i < numThreads; i++) {
				Thread thread = new Thread(crawler, String.valueOf(i));
				thread.start();

				threads.add(thread);
			}

			log.info("created " + numThreads + " threads");
		}

		@EventListener
		public void onCrawlingFinished(DocumentsStore.CrawlingFinishedEvent event) {
			if (threads.size() > 0) {
				log.info("received CrawlingFinishedEvent, interrupting threads");

				for (Thread thread : threads) {
					thread.interrupt();
				}
				threads.clear();

				log.info("interrupted all threads");
			} else {
				log.info("received CrawlingFinishedEvent but there is no threads to interrupt");
			}
		}
	}
}
