package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.DBUtils;
import com.cufe.searchengine.util.Patterns;
import com.cufe.searchengine.util.SnippetExtractor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

// TODO: abstract tables accessing into DocumentsTable
public class Document {
	private long rowID;
	private String content;
	private String url;
	private long timeMillis;
	private long indexTimeMillis;
	private int counter;

	public Document(String content, String url, long timeMillis) {
		this.content = content;
		this.url = url;
		this.timeMillis = timeMillis;
		this.counter = -1;
	}

	public Document rowID(long rowID) {
		this.rowID = rowID;
		return this;
	}

	public Document counter(int counter) {
		this.counter = counter;
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public int store(JdbcTemplate jdbcTemplate) throws Exception {
		if (counter == -1) {
			throw new IllegalStateException("counter is not set");
		}

		return DBUtils.waitLock(100,
			() -> jdbcTemplate.update("REPLACE INTO documents(url, content, timeMillis, counter) " +
				"VALUES(?, ?, ?, ?);", url, content, timeMillis, counter)
		);
	}

	public long getRowID() {
		return rowID;
	}

	public void setRowID(long rowID) {
		this.rowID = rowID;
	}

	public Document indexTimeMillis(long indexTimeMillis) {
		this.indexTimeMillis = indexTimeMillis;
		return this;
	}

	public long getIndexTimeMillis() {
		return indexTimeMillis;
	}

	public void setIndexTimeMillis(long indexTimeMillis) {
		this.indexTimeMillis = indexTimeMillis;
	}

	public String getTitle() {
		String title = Patterns.extractHtmlTitle(this.getContent());
		return title.equals("") ? this.getUrl() : title;
	}

	public String getSnippet(List<String> keywords) {
		return SnippetExtractor.extract(content, keywords);
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
