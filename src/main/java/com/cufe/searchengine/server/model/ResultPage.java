package com.cufe.searchengine.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ResultPage
 */
@javax.annotation.Generated(value = "com.cufe.searchengine.codegen.languages.SpringCodegen", date = "2020-03-23T21:49:05.724243+02:00[Africa/Cairo]")

public class ResultPage {
	@JsonProperty("currentPage")
	private Integer currentPage;

	@JsonProperty("totalPages")
	private Integer totalPages;

	@JsonProperty("results")
	@Valid
	private List<QueryResult> results = null;

	public ResultPage currentPage(Integer currentPage) {
		this.currentPage = currentPage;
		return this;
	}

	/**
	 * Get currentPage
	 *
	 * @return currentPage
	 */
	@ApiModelProperty(value = "")


	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public ResultPage totalPages(Integer totalPages) {
		this.totalPages = totalPages;
		return this;
	}

	/**
	 * Get totalPages
	 *
	 * @return totalPages
	 */
	@ApiModelProperty(value = "")


	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public ResultPage results(List<QueryResult> results) {
		this.results = results;
		return this;
	}

	public ResultPage addResultsItem(QueryResult resultsItem) {
		if (this.results == null) {
			this.results = new ArrayList<>();
		}
		this.results.add(resultsItem);
		return this;
	}

	/**
	 * Get results
	 *
	 * @return results
	 */
	@ApiModelProperty(value = "")

	@Valid

	public List<QueryResult> getResults() {
		return results;
	}

	public void setResults(List<QueryResult> results) {
		this.results = results;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ResultPage resultPage = (ResultPage) o;
		return Objects.equals(this.currentPage, resultPage.currentPage) &&
			Objects.equals(this.totalPages, resultPage.totalPages) &&
			Objects.equals(this.results, resultPage.results);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentPage, totalPages, results);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ResultPage {\n");

		sb.append("    currentPage: ").append(toIndentedString(currentPage)).append("\n");
		sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
		sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

