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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
@Api(value = "trends", description = "the trends API")
public class TrendsApiController {
	private final NativeWebRequest request;
	private final QueryProcessor queryProcessor;

	@Autowired
	public TrendsApiController(NativeWebRequest request, QueryProcessor queryProcessor) {
		this.request = request;
		this.queryProcessor = queryProcessor;
	}

	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	/**
     * GET /api/trends : get list of 10 most searched persons of given country.
     *
     * @param country country alpha-3 code (ISO 3166) all capital (required)
     * @return successful operation, result mustn&#39;t be empty (status code 200)
     *         or invalid country code (status code 404)
     */
    @ApiOperation(value = "get list of 10 most searched persons of given country.", nickname = "trends", notes = "", response = String.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation, result mustn't be empty", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "invalid country code") })
    @RequestMapping(value = "/api/trends",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<String>> trends(@NotNull @ApiParam(value = "country alpha-3 code (ISO 3166) all capital", required = true) @Valid @RequestParam(value = "country", required = true) String country) {
		if ("1".equals(System.getenv("MOCK"))) {
			ArrayList<String> persons = new ArrayList<>();
			persons.add("Mahmoud Adas");
			persons.add("Mahmoud Othman Adas");
			persons.add("Shrek");
			persons.add("Mahmoud Adas Again");
			persons.add("Maybe Mahmoud Osman Adas");
			persons.add("Adas Adas");
			persons.add("Yup, it's 3ds ᕕ( ᐛ )ᕗ");
			persons.add("Of course adas is the most trndy, what did you expect?");
			persons.add("No not adas again");
			persons.add("Adas (✿\u2060´ ꒳ ` )");

			return ResponseEntity.ok(persons);
		} else {
			// TODO
			return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		}
    }
}
