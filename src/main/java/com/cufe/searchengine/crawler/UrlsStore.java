package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class UrlsStore {
	private static final Logger log = LoggerFactory.getLogger(UrlsStore.class);

	private final BlockingQueue<String> store;
	@Autowired
	private RobotsStore robotsStore;

	public UrlsStore(@Value("${crawler.seedFile}") Resource crawlerSeedResource) throws IOException {
		store = new LinkedBlockingQueue<>(StringUtils.resourceToLines(crawlerSeedResource));
		log.info("seeds.size=" + store.size());
		// TODO: implement priority for plling urls
	}

	public void add(String url) throws InterruptedException {
		if (store.contains(url)) {
			return;
		}

		if (!robotsStore.canRequest(url, () -> store.remove(url))) {
			return;
		}

		// TODO: ignore duplicate urls

		store.put(url);
	}

	public String poll() throws InterruptedException {
		return store.take();
	}

	public int size() {
		return store.size();
	}
}
