package com.cufe.searchengine.crawler;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: implement priority for pulling urls
// TODO: better seed set

@Component
public class UrlsStore {
	private static final Logger log = LoggerFactory.getLogger(UrlsStore.class);

	private final BlockingQueue<String> store = new LinkedBlockingQueue<>();
	@Autowired
	private RobotsStore robotsStore;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DocumentsStore documentsStore;
	@Value("${crawler.urlsStore.saveStateWaitMillis}")
	private long saveStateWaitMillis;
	@Value("${crawler.seedFile}")
	private Resource crawlerSeedResource;

	@EventListener
	public void onDBInitialized(DBInitializer.DBInitializedEvent event) throws IOException {
		List<String> urls = jdbcTemplate.queryForList("SELECT url FROM urlstore_queue;", String.class);
		if (urls.size() > 0) {
			log.info("urls loaded from db.size() = " + urls.size());
			store.addAll(urls);
		} else {
			store.addAll(StringUtils.resourceToLines(crawlerSeedResource));
			log.info("loaded seeds of size=" + store.size());
		}
	}

	@PreDestroy
	private void onDestroy() {
		synchronized (store) {
			if (store.size() == 0) {
				log.info("won't save state, it's empty");
				return;
			}

			log.info("before closing, will save state");

			// flush
			jdbcTemplate.execute("DELETE FROM urlstore_queue;");
			log.info("flushed urlstore_queue table");

			// commit
			StringBuilder sql = new StringBuilder("INSERT INTO urlstore_queue VALUES");
			for (int i = 0; i < store.size() - 1; i++) {
				sql.append("(?),");
			}
			sql.append("(?);");

			jdbcTemplate.update(sql.toString(), store.toArray());

			log.info("saved " + store.size() + " url/s");
		}
	}

	public void add(String url) throws InterruptedException {
		if (url == null || store.contains(url)) {
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
