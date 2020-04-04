package com.cufe.searchengine.indexer;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.KeywordsTable;
import com.cufe.searchengine.util.DocumentFilterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
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
	private DocumentsTable documentsTable;
	@Autowired
	private KeywordsTable keywordsTable;

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

		int rows = documentsTable.updateIndexTime(documents);

		log.info("updated rows={}, docs={}", rows, documents.size());
	}


	private void updateKeyword(List<String> words, long docID) throws Exception {
		if (words.size() == 0) {
			return;
		}

		keywordsTable.insertOrIgnore(words, docID);
	}

	private List<Document> fetchNonIndexedDocs() throws Exception {
		return documentsTable.selectAll(maxDocumentsPerIteration);
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
