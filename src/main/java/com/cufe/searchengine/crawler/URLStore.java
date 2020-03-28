package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class URLStore {
	private static final Logger log = LoggerFactory.getLogger(URLStore.class);

	private final ConcurrentLinkedQueue<String> seeds;

	public URLStore(@Value("${crawler.seedFile}") Resource crawlerSeedResource) throws IOException {
		seeds = new ConcurrentLinkedQueue<>(StringUtils.resourceToLines(crawlerSeedResource));
		log.info("seeds.size=" + seeds.size());
	}

	public boolean add(String url) {
		synchronized (seeds) {
			if (seeds.contains(url)) {
				return false;
			}

			boolean tmp = seeds.add(url);
			seeds.notify();
			return tmp;
		}
	}

	public String poll() {
		synchronized (seeds) {
			if (seeds.size() == 0) {
				try {
					seeds.wait();
				} catch (InterruptedException ignored) {
				}
			}

			return seeds.poll();
		}
	}

	public int size() {
		return seeds.size();
	}
}
