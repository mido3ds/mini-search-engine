package com.cufe.searchengine.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CrawlersRunner implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CrawlersRunner.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Value("${crawler.userAgent}")
	private String userAgent;
	@Value("${crawler.numThreads}")
	private int numThreads;
	@Autowired
	private URLStore urlStore;

	@Override
	public void run(String... args) {
		log.info("creating " + numThreads + " of threads of crawler, with seedSet of size " + urlStore.size());

		for (int i = 0; i < numThreads; i++) {
			new Thread(new Crawler(jdbcTemplate, userAgent, urlStore), String.valueOf(i)).start();
		}
	}
}
