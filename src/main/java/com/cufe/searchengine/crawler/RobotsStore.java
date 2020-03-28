package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.Cache;
import com.cufe.searchengine.util.HttpPattern;
import com.panforge.robotstxt.RobotsTxt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class RobotsStore {
	private final Logger log = LoggerFactory.getLogger(RobotsStore.class);

	/**
	 * Key String is the url of the website
	 */
	private Cache<String, RobotsTxt> cache = new Cache<>();

	@Value("${crawler.userAgent}")
	private String userAgent;
	@Value("${crawler.robotsStore.cacheTimeoutMillis}")
	private long cacheTimeoutMillis;

	public boolean canRequest(String url, Runnable callback) {
		try {
			url = HttpPattern.extractWebsite(url);
			if (url.equals("")) {
				return false;
			}
		} catch (Exception ignored) {
			return false;
		}

		RobotsTxt robotsTxt = cache.get(url);
		if (robotsTxt != null) {
			return robotsTxt.query(userAgent, url);
		}

		String threadUrl = url;
		new Thread(() -> {
			try {
				RobotsTxt r = loadRobots(threadUrl);
				if (r == null) {
					callback.run();
				} else {
					cache.put(threadUrl, r, cacheTimeoutMillis);
					log.info("inserted into cache the robots.txt for "+threadUrl);
				}
			} catch (IOException ignored) {
				callback.run();
			}
		}).start();

		return true;
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
