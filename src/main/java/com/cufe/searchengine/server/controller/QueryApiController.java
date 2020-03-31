package com.cufe.searchengine.server.controller;

import com.cufe.searchengine.query.QueryProcessor;
import com.cufe.searchengine.server.model.QueryResult;
import com.cufe.searchengine.server.model.ResultPage;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
@Api(value = "query", description = "the query API")
public class QueryApiController {
	private final NativeWebRequest request;
	private final QueryProcessor queryProcessor;

	@Autowired
	public QueryApiController(NativeWebRequest request, QueryProcessor queryProcessor) {
		this.request = request;
		this.queryProcessor = queryProcessor;
	}

	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	/**
	 * GET /api/query : submit a query
	 *
	 * @param q    string to search for (required)
	 * @param page page of results to fetch, default 1 (optional)
	 * @return successful operation, result could be empty (status code 200)
	 */
	@ApiOperation(value = "submit a query", nickname = "query", notes = "", response = ResultPage.class, tags = {})
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "successful operation, result could be empty", response = ResultPage.class)
	})
	@RequestMapping(value = "/api/query", produces = {"application/json"}, method = RequestMethod.GET)
	ResponseEntity<ResultPage> query(
		@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q",
			required = true) String q,
		@ApiParam(value = "page of results to fetch, default 1") @Valid @RequestParam(value = "page",
			required = false) Integer page
	) {
		page = page == null ? 1 : page;

		List<QueryResult> queryResults = queryProcessor.search(q);

		int pages = (int) Math.ceil(queryResults.size() / 10.0d);

		if (page > pages) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		List<QueryResult> subList = queryResults.subList((page - 1) * 10, Math.min(page * 10, queryResults.size()));

		return ResponseEntity.ok(new ResultPage().currentPage(page).totalPages(pages).results(subList));
	}
}
