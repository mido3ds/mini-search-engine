package com.cufe.searchengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * QueryResult
 */
@javax.annotation.Generated(value = "com.cufe.searchengine.codegen.languages.SpringCodegen", date = "2020-03-14T12:05:55.435057+02:00[Africa/Cairo]")

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

	/**
	 * Get title
	 *
	 * @return title
	 */
	@ApiModelProperty(value = "")


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

	/**
	 * Get link
	 *
	 * @return link
	 */
	@ApiModelProperty(value = "")


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

	/**
	 * Get snippet
	 *
	 * @return snippet
	 */
	@ApiModelProperty(value = "")


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
		StringBuilder sb = new StringBuilder();
		sb.append("class QueryResult {\n");

		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("    link: ").append(toIndentedString(link)).append("\n");
		sb.append("    snippet: ").append(toIndentedString(snippet)).append("\n");
		sb.append("}");
		return sb.toString();
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

