package com.cufe.searchengine.api;

import com.cufe.searchengine.model.QueryResult;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "com.cufe.searchengine.codegen.languages.SpringCodegen", date = "2020-03-14T12:05:55.435057+02:00[Africa/Cairo]")

@Controller
@RequestMapping("${openapi.miniSearchEngine.base-path:}")
@Validated
@Api(value = "query", description = "the query API")
public class QueryApiController {
	private final NativeWebRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public QueryApiController(NativeWebRequest request) {
		this.request = request;
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
	@ApiOperation(value = "submit a query", nickname = "query", notes = "", response = QueryResult.class, responseContainer = "List", tags = {})
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "successful operation, result could be empty", response = QueryResult.class, responseContainer = "List")})
	@RequestMapping(value = "/api/query",
		produces = {"application/json"},
		method = RequestMethod.GET)
	ResponseEntity<List<QueryResult>> query(@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q", required = true) String q, @ApiParam(value = "page of results to fetch, default 1") @Valid @RequestParam(value = "page", required = false) Integer page) {
		ArrayList<QueryResult> queryResults = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			queryResults.add(
				new QueryResult()
					.title("Wikipedia")
					.link("https://www.wikipedia.org")
					.snippet("Wikipedia is a free online encyclopedia, " +
						"created and edited by volunteers around the world and hosted " +
						"by the Wikimedia Foundation.")
			);
		}

		return ResponseEntity.ok(queryResults);
	}
}
