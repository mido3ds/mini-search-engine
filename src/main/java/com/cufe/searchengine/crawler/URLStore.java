package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class URLStore {
	private static final Logger log = LoggerFactory.getLogger(URLStore.class);

	private final ConcurrentLinkedQueue<String> store;
	@Autowired
	private RobotsStore robotsStore;

	public URLStore(@Value("${crawler.seedFile}") Resource crawlerSeedResource) throws IOException {
		store = new ConcurrentLinkedQueue<>(StringUtils.resourceToLines(crawlerSeedResource));
		log.info("seeds.size=" + store.size());
	}

	public boolean add(String url) {
		synchronized (store) {
			if (store.contains(url)) {
				return false;
			}

			if (!robotsStore.canRequest(url, () -> store.remove(url))) {
				return false;
			}

			boolean tmp = store.add(url);
			store.notify();
			return tmp;
		}
	}

	public String poll() {
		synchronized (store) {
			if (store.size() == 0) {
				try {
					store.wait();
				} catch (InterruptedException ignored) {
				}
			}

			return store.poll();
		}
	}

	public int size() {
		return store.size();
	}
}
