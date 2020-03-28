package com.cufe.searchengine.crawler;

import org.springframework.jdbc.core.JdbcTemplate;

public class Document {
	private String content;
	private String url;
	private long timeMillis;

	public Document(String content, String url, long timeMillis) {
		this.content = content;
		this.url = url;
		this.timeMillis = timeMillis;
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

	public void store(JdbcTemplate jdbcTemplate) throws Exception {
		int rows = jdbcTemplate.update("INSERT INTO documents VALUES(?, ?, ?);",
			url,
			content,
			timeMillis);

		if (rows != 1) {
			throw new Exception("storing failed with updated rows="+rows);
		}
	}
}
