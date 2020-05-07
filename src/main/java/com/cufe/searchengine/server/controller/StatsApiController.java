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
@Api(value = "stats", description = "the stats API")
public class StatsApiController {
	private final NativeWebRequest request;
	private final QueryProcessor queryProcessor;

	@Autowired
	public StatsApiController(NativeWebRequest request, QueryProcessor queryProcessor) {
		this.request = request;
		this.queryProcessor = queryProcessor;
	}

	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	/**
     * GET /api/stats : performance analysis and statistics
     *
     * @return statistics and analysis (status code 200)
     */
    @ApiOperation(value = "performance analysis and statistics", nickname = "performanceStats", notes = "", response = Object.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "statistics and analysis", response = Object.class) })
    @RequestMapping(value = "/api/stats",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Object> performanceStats() {
        // TODO
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
