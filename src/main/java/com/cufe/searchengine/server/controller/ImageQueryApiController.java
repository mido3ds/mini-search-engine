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
@Api(value = "image/query", description = "the image/query API")
public class ImageQueryApiController {
	private final NativeWebRequest request;
	private final QueryProcessor queryProcessor;

	@Autowired
	public ImageQueryApiController(NativeWebRequest request, QueryProcessor queryProcessor) {
		this.request = request;
		this.queryProcessor = queryProcessor;
	}

	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	/**
     * GET /api/image/query : get list of urls to images that are associated with given search terms
     *
     * @param q string to search for (required)
     * @param page page of results to fetch, default 1 (optional)
     * @return successful operation, result mustn&#39;t be empty (status code 200)
     *         or no results (status code 404)
     */
    @ApiOperation(value = "get list of urls to images that are associated with given search terms", nickname = "imageQuery", notes = "", response = ResultPage.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation, result mustn't be empty", response = ResultPage.class),
        @ApiResponse(code = 404, message = "no results") })
    @RequestMapping(value = "/api/image/query",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResultPage> imageQuery(@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q", required = true) String q,@ApiParam(value = "page of results to fetch, default 1") @Valid @RequestParam(value = "page", required = false) Integer page) {
		// TODO
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
