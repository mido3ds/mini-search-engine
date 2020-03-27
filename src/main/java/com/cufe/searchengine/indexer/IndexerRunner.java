package com.cufe.searchengine.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class IndexerRunner implements CommandLineRunner {
	private final Logger log = LoggerFactory.getLogger(IndexerRunner.class);

	@Value("${indexer.waitTimeMillis}")
	private int sleepTimeMillis;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) {
		log.info("sleepTimeMillis = " + sleepTimeMillis);

		new Thread(new Indexer(sleepTimeMillis, jdbcTemplate)).start();
	}
}
