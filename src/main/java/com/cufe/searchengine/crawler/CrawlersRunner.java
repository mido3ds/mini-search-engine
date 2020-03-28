package com.cufe.searchengine.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CrawlersRunner implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CrawlersRunner.class);

	@Value("${crawler.numThreads}")
	private int numThreads;
	@Autowired
	private Crawler crawler;

	@Override
	public void run(String... args) {
		log.info("creating " + numThreads + " of threads of crawler");

		for (int i = 0; i < numThreads; i++) {
			new Thread(crawler, String.valueOf(i)).start();
		}
	}
}
