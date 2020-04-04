package com.cufe.searchengine.query;

import com.cufe.searchengine.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Ranker implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Ranker.class);

	@Value("${ranker.waitTimeMillis}")
	private long waitTimeMillis;

	@Override
	public void run() {
		log.info("started");

		while (true) {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException ignored) {
			}

			// TODO: calculate rank and reassign it
		}
	}

	@Component
	private static class RankerRunner {
		@Autowired
		private Ranker ranker;

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("received DBInitialized event");

			new Thread(ranker, "ranker").start();
		}
	}
}
