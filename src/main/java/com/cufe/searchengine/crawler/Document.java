package com.cufe.searchengine.crawler;

import com.cufe.searchengine.util.Patterns;
import com.cufe.searchengine.util.SnippetExtractor;

import java.util.List;

public class Document {
	private long rowID;
	private String content;
	private String url;
	private long timeMillis;
	private long indexTimeMillis;
	private int counter;
	private float rank;
	private int wordCount;
	private String pubDate;
	private String countryCode;

	public Document(String content, String url, long timeMillis, float rank, int wordCount, String pubDate, String countryCode) {
		this.content = content;
		this.url = url;
		this.timeMillis = timeMillis;
		this.counter = -1;
		this.rank = rank;
		this.wordCount = wordCount;
		this.pubDate = pubDate;
		this.countryCode = countryCode;
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

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTitle() {
		String title = Patterns.extractHtmlTitle(this.getContent());
		return title.equals("") ? this.getUrl() : title;
	}

	public String getSnippet(List<String> keywords) {
		return SnippetExtractor.extract(content, keywords);
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
