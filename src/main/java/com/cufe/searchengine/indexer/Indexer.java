package com.cufe.searchengine.indexer;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.util.DBUtils;
import com.cufe.searchengine.util.DocumentFilterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
				int totalWords = 0;

				List<Document> documents = fetchNonIndexedDocs();
				for (Document document : documents) {
					List<String> keywords = DocumentFilterer.keywordsFromHtml(document.getContent());
					updateKeyword(keywords, document.getRowID());

					totalWords += keywords.size();
				}
				updateDocsIndexTime(documents);

				log.info("indexed {} documents with {} words", documents.size(), totalWords);
			} catch (Exception e) {
				log.error("exception = {} {}", e.getMessage(), e.getStackTrace());
			}
		}

		throw new RuntimeException("indexer interrupted");
	}

	private void updateDocsIndexTime(List<Document> documents) throws Exception {
		if (documents.size() == 0) {
			return;
		}

		StringBuilder builder = new StringBuilder("UPDATE documents SET indexTimeMillis = ? WHERE ROWID in (");
		for (int i = 0; i < documents.size() - 1; i++) {
			builder.append(documents.get(i).getRowID()).append(",");
		}
		builder.append(documents.get(documents.size() - 1).getRowID()).append(");");

		int rows = DBUtils.waitLock(100, () -> jdbcTemplate.update(builder.toString(), System.currentTimeMillis()));
		if (rows != documents.size()) {
			throw new RuntimeException("should have updated " + documents.size());
		}

		log.info("updated rows={}, docs={}", rows, documents.size());
	}

	private void updateKeyword(List<String> words, long docID) throws Exception {
		if (words.size() == 0) {
			return;
		}

		StringBuilder builder = new StringBuilder("INSERT OR IGNORE INTO keywords VALUES");
		for (int i = 0; i < words.size(); i++) {
			builder.append("(?)");

			if (i != words.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";\n");

		builder.append("REPLACE INTO keywords_documents(docID, wordID) VALUES");
		for (int i = 0; i < words.size(); i++) {
			builder.append("(").append(docID).append(",last_insert_rowid()-").append(i).append(")");

			if (i != words.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";");

		DBUtils.waitLock(100, () -> jdbcTemplate.update(builder.toString(), words.toArray()));
	}

	private List<Document> fetchNonIndexedDocs() throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.query(getDocQueryString(),
			(row, i) -> new Document(row.getString(1), row
				.getString(2), row.getLong(3)).indexTimeMillis(row.getLong(4))
				.rowID(row.getLong(5))));
	}

	private String getDocQueryString() {
		return String.format("SELECT content, url, timeMillis, indexTimeMillis, " + "ROWID FROM documents WHERE " +
			"indexTimeMillis < timeMillis LIMIT %d;", maxDocumentsPerIteration);
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
