package com.cufe.searchengine.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class QueryResult {
	@JsonProperty("title")
	private String title;

	@JsonProperty("link")
	private String link;

	@JsonProperty("snippet")
	private String snippet;

	public QueryResult title(String title) {
		this.title = title;
		return this;
	}

	@ApiModelProperty()
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public QueryResult link(String link) {
		this.link = link;
		return this;
	}

	@ApiModelProperty()
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public QueryResult snippet(String snippet) {
		this.snippet = snippet;
		return this;
	}

	@ApiModelProperty()
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		QueryResult queryResult = (QueryResult) o;
		return Objects.equals(this.title, queryResult.title) &&
			Objects.equals(this.link, queryResult.link) &&
			Objects.equals(this.snippet, queryResult.snippet);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, link, snippet);
	}

	@Override
	public String toString() {
		return "class QueryResult {\n" +
			"    title: " + toIndentedString(title) + "\n" +
			"    link: " + toIndentedString(link) + "\n" +
			"    snippet: " + toIndentedString(snippet) + "\n" +
			"}";
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}

