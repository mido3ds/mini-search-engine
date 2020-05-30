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
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
			required = false) Integer page, 
		HttpServletRequest request
	) {
		page = page == null ? 1 : page;

		if ("1".equals(System.getenv("MOCK"))) {
			int pages = 10;

			if (page > pages) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			ArrayList<QueryResult> queryResults = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				queryResults.add(new QueryResult()
						.title("Wikipedia").link("https://www.wikipedia.org")
						.snippet("<em>Wikipedia</em> is a free online encyclopedia, " +
								"created and edited by volunteers around the world and hosted by the " +
								"Wikimedia Foundation."));
			}

			return ResponseEntity.ok(new ResultPage().currentPage(page).totalPages(pages).results(queryResults));
		} else {
			List<QueryResult> queryResults = queryProcessor.search(q, request.getRemoteAddr(), false);

			int pages = (int) Math.ceil(queryResults.size() / 10.0d);

			if (page > pages) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			List<QueryResult> subList = queryResults.subList((page - 1) * 10, Math.min(page * 10, queryResults.size()));

			return ResponseEntity.ok(new ResultPage().currentPage(page).totalPages(pages).results(subList));
		}
	}
}
