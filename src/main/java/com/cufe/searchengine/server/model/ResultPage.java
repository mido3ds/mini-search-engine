package com.cufe.searchengine.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@ApiModelProperty()
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

	@ApiModelProperty()
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

	@ApiModelProperty()
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
		return Objects.equals(this.currentPage, resultPage.currentPage) && Objects.equals(this.totalPages,
			resultPage.totalPages) && Objects
			.equals(this.results, resultPage.results);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentPage, totalPages, results);
	}

	@Override
	public String toString() {
		return "class ResultPage {\n" + "    currentPage: " + toIndentedString(currentPage) + "\n" + "    totalPages: "
			+ toIndentedString(totalPages) + "\n" + "    results: " + toIndentedString(results) + "\n" + "}";
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

