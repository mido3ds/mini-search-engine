package com.cufe.searchengine.server.controller;

import com.cufe.searchengine.query.QueryProcessor;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "complete", description = "the complete API")
public class CompleteApiController {
	private final NativeWebRequest request;
	private final QueryProcessor queryProcessor;

	@Autowired
	public CompleteApiController(NativeWebRequest request, QueryProcessor queryProcessor) {
		this.queryProcessor = queryProcessor;
		this.request = request;
	}

	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	/**
	 * GET /api/complete : get list of completions
	 *
	 * @param q string to search for (required)
	 * @return successful operation, result could be empty (status code 200)
	 */
	@ApiOperation(value = "get list of completions", nickname = "complete", notes = "", response = String.class,
		responseContainer = "List", tags = {})
	@ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation, result mustn't be empty", response = String.class,
			responseContainer = "List"),
        @ApiResponse(code = 404, message = "no results") })
	@RequestMapping(value = "/api/complete", produces = {"application/json"}, method = RequestMethod.GET)
	ResponseEntity<List<String>> complete(
		@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q",
			required = true) String q
	) {
		return ResponseEntity.ok(queryProcessor.suggest(q));
	}
}
