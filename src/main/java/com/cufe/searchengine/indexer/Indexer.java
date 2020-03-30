package com.cufe.searchengine.indexer;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteException;

import java.util.List;

@Component
public class Indexer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Indexer.class);

	@Value("${indexer.waitTimeMillis}")
	private int sleepTimeMillis;
	@Value("${indexer.maxDocumentsPerIteration}")
	private int maxDocumentsPerIteration;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void run() {
		log.info("started");

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(sleepTimeMillis);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			try {
				List<Document> documents = fetchNonIndexedDocs();
				for (Document document : documents) {
					KeywordsExtractor
						.extract(document.getContent())
						.forEach((w) -> updateKeyword(w, document.getRowID()));
				}
				updateDocsIndexTime(documents);

				log.info("indexed {} documents", documents.size());
			} catch (Exception e) {
				log.error("exception = {}", e.getMessage());
			}
		}

		log.info("interrupted, closing");
	}

	private void updateDocsIndexTime(List<Document> documents) {
		StringBuilder builder = new StringBuilder("UPDATE documents SET indexTimeMillis = ? WHERE ROWID in (");
		for (int i = 0; i < documents.size() - 1; i++) {
			builder.append(documents.get(i).getRowID());
			builder.append(",");
		}
		builder.append(documents.get(documents.size()-1).getRowID());
		builder.append(");");

		int rows = jdbcTemplate.update(builder.toString(), System.currentTimeMillis());
		if (rows != documents.size()) {
			throw new RuntimeException("should have updated "+documents.size());
		}

		log.info("updated rows={}, docs={}", rows, documents.size());
	}

	// TODO: optimize
	private void updateKeyword(String word, long docID) {
		long l = System.currentTimeMillis();

		jdbcTemplate.update("INSERT OR IGNORE INTO keywords VALUES(?);", word);

		Long wordID = jdbcTemplate.query("SELECT ROWID FROM keywords WHERE word=?;",
			(row, i) -> row.getLong(1), word).get(0);

		int rows = jdbcTemplate.update("REPLACE INTO keywords_documents(docID, wordID) VALUES(?, ?)", docID, wordID);
		if (rows != 1) {
			throw new RuntimeException("couldn't insert "+word+" into keywords_documents");
		}

		log.info("inserted word: {} took:{}", word, System.currentTimeMillis() - l);
	}

	private List<Document> fetchNonIndexedDocs() {
		return jdbcTemplate.query(
			String.format("SELECT content, url, timeMillis, indexTimeMillis, ROWID " +
				"FROM documents WHERE indexTimeMillis < timeMillis LIMIT %d;", maxDocumentsPerIteration),
			(row, i) -> new Document(
				row.getString(1),
				row.getString(2),
				row.getLong(3)
			)
				.indexTimeMillis(row.getLong(4))
				.rowID(row.getLong(5))
		);
	}

	@Component
	private static class IndexerRunner {
		private final Logger log = LoggerFactory.getLogger(IndexerRunner.class);

		@Autowired
		private Indexer indexer;

		@EventListener
		public void onDBInitialized(DBInitializer.DBInitializedEvent event) {
			log.info("received DBInitializedEvent, starting Indexer");

			new Thread(indexer, "indexer").start();
		}
	}
}
