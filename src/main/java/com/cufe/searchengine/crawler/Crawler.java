package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.StringUtils;
import com.panforge.robotstxt.RobotsTxt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Crawler implements Runnable {
	private static final String HTTP_URL_PATTERN = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
	private static final Logger log = LoggerFactory.getLogger(Crawler.class);

	private final JdbcTemplate jdbcTemplate;
	private final String userAgent;
	private final URLStore urlStore;

	public Crawler(JdbcTemplate jdbcTemplate, String userAgent, URLStore urlStore) {
		this.jdbcTemplate = jdbcTemplate;
		this.userAgent = userAgent;
		this.urlStore = urlStore;
	}

	private static String[] extractURLs(String html) {
		return Pattern.compile(HTTP_URL_PATTERN)
			.matcher(html)
			.results()
			.map(MatchResult::group)
			.toArray(String[]::new);
	}

	@Override
	public void run() {
		log.info("started");

		// TODO

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

	private RobotsTxt loadRobots(String link) throws IOException {
		InputStream robotsTxtStream = null;
		RobotsTxt robotsTxt;

		URL url = new URL(link + "/robots.txt");
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("User-Agent", userAgent);

		try {
			robotsTxtStream = urlConnection.getInputStream();
			robotsTxt = RobotsTxt.read(robotsTxtStream);
		} finally {
			if (robotsTxtStream != null) {
				robotsTxtStream.close();
			}
		}

		return robotsTxt;
	}
}
