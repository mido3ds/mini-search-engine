package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.HttpHtmlPattern;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Document {
	private long rowID;
	private String content;
	private String url;
	private long timeMillis;
	private long indexTimeMillis;

	public Document(String content, String url, long timeMillis) {
		this.content = content;
		this.url = url;
		this.timeMillis = timeMillis;
	}

	public Document rowID(long rowID) {
		this.rowID = rowID;
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

	public int store(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.update("REPLACE INTO documents(url, content, timeMillis) VALUES(?, ?, ?);",
			url,
			content,
			timeMillis);
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
		String title = HttpHtmlPattern.extractHtmlTitle(this.getContent());
		title = title.equals("")? this.getUrl():title;
		return title;
	}

	public String getSnippet(List<String> keywords) {
		return "Some Snippet TODO"; // TODO
	}
}
