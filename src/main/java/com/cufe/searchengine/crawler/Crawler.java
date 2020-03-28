package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.HttpPattern;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

		while (!Thread.currentThread().isInterrupted() && !documentsStore.isFull()) {
			String url = null;
			try {
				url = urlsStore.poll();
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			String document = null;

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
			for (String u : urls) {
				try {
					urlsStore.add(u);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
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
}
