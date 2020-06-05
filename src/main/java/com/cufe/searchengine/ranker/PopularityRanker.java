package com.cufe.searchengine.ranker;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.OutgoingURLsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PopularityRanker implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(PopularityRanker.class);

	@Value("${popularityRanker.waitTimeMillis}")
	private long waitTimeMillis;

	@Autowired
	private DocumentsTable documentsTable;
	@Autowired
	private OutgoingURLsTable outgoingURLsTable;

	@Override
	public void run() {
		log.info("started");

		while (true) {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException ignored) {
			}

			Hashtable<String, Float> urlRanks = new Hashtable<String, Float>();
			Hashtable<String, Integer> outgoingURLNum = new Hashtable<String, Integer>();
			Hashtable<String, List<String>> incomingURLs = new Hashtable<String, List<String>>();

			try {
				urlRanks =  documentsTable.selectAllURLRanks();
			} catch (Exception e) {
				log.error("error extracting ranks [" + e.getMessage() + "]");
				continue;
			}
			try {
				outgoingURLNum =  outgoingURLsTable.getAllOutgoingCount();
			} catch (Exception e) {
				log.error("error extracting outgoing urls number [" + e.getMessage() + "]");
				continue;
			}
			try {
				incomingURLs = outgoingURLsTable.selectAllIncomingURLs();
			} catch (Exception e) {
				log.error("error extracting incoming urls [" + e.getMessage() + "]");
				continue;
			}

			log.info("reseting ranks");
			Integer urlNum = urlRanks.size();
			Enumeration urlKeys = urlRanks.keys(); 

			while (urlKeys.hasMoreElements()) { 
				urlRanks.put((String) urlKeys.nextElement(), (float) 1.0/urlNum);
			} 

			log.info("calculating new ranks");
			Hashtable<String, Float> newURLRanks = new Hashtable<String, Float>();
			newURLRanks = (Hashtable<String, Float>) urlRanks.clone();

			for (int i=0; i<5; i++) { // five iterations of pagerank
				urlKeys = incomingURLs.keys();
				while (urlKeys.hasMoreElements()) { 
					Float rank = (float) 0.0;
					String currentURL = (String) urlKeys.nextElement();
					for (String url : incomingURLs.get(currentURL)) {
						try {
							rank += urlRanks.get(url) / outgoingURLNum.get(url);
						} catch (NullPointerException e) {
							continue;
						}
					}
					newURLRanks.put(currentURL, rank);
				} 
				urlRanks = (Hashtable<String, Float>) newURLRanks.clone();
			}

			log.info("inserting new ranks to database");
			try {
				documentsTable.updateAllURLRanks(urlRanks);
			} catch (Exception e) {
				log.error("error updating ranks in database [" + e.getMessage() + "]");
				continue;
			}
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
