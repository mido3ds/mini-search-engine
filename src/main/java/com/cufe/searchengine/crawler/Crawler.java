package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.HttpPattern;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Crawler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Crawler.class);

	private final JdbcTemplate jdbcTemplate;
	private final String userAgent;
	private final URLStore urlStore;

	public Crawler(JdbcTemplate jdbcTemplate, String userAgent, URLStore urlStore) {
		this.jdbcTemplate = jdbcTemplate;
		this.userAgent = userAgent;
		this.urlStore = urlStore;
	}

	@Override
	public void run() {
		log.info("started");

		// TODO: end condition
		while (true) {
			String url = urlStore.poll();
			String document = null;

			try {
				document = loadURL(url);
			} catch (Exception e) {
				log.error("url="+url+",error="+e.getMessage());
				continue;
			}

			if (document.length() == 0) {
				continue;
			}

			String[] urls = HttpPattern.extractURLs(document);
			log.info("extracted "+urls.length+" urls from "+url+", document has size="+document.length());
			for (String u : urls) {
				urlStore.add(u);
			}

			// TODO: store document
			// TODO: apply politeness limits
		}

//		log.info("ended");
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
