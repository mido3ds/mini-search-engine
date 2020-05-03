package com.cufe.searchengine.query;

import com.cufe.searchengine.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PopularityRanker implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(PopularityRanker.class);

	@Value("${popularityRanker.waitTimeMillis}")
	private long waitTimeMillis;

	@Override
	public void run() {
		log.info("started");

		while (true) {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException ignored) {
			}

			// TODO: calculate pagerank and insert to db
		}
	}

	@Component
	private static class PopularityRankerRunner {
		@Autowired
		private PopularityRanker popularityRanker;

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("received DBInitialized event");

			new Thread(popularityRanker, "popularity_ranker").start();
		}
	}
}
