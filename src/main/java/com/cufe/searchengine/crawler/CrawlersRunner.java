package com.cufe.searchengine.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlersRunner implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CrawlersRunner.class);

	@Value("${crawler.numThreads}")
	private int numThreads;
	@Autowired
	private Crawler crawler;
	private List<Thread> threads = new ArrayList<>();

	@Override
	public void run(String... args) {
		log.info("creating " + numThreads + " of threads of crawler");

		for (int i = 0; i < numThreads; i++) {
			Thread thread = new Thread(crawler, String.valueOf(i));
			thread.start();

			threads.add(thread);
		}
	}

	@EventListener
	public void handleCrawlingFinishedEvent(CrawlingFinishedEvent event) {
		log.info("received CrawlingFinishedEvent, interrupting threads");

		for (Thread thread : threads) {
			thread.interrupt();
		}
	}
}
