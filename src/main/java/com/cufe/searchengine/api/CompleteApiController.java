package com.cufe.searchengine.api;

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
@Api(value = "complete", description = "the complete API")
public class CompleteApiController {
	private final NativeWebRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public CompleteApiController(NativeWebRequest request) {
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
	@ApiOperation(value = "get list of completions", nickname = "complete", notes = "", response = String.class, responseContainer = "List", tags = {})
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "successful operation, result could be empty", response = String.class, responseContainer = "List")})
	@RequestMapping(value = "/api/complete",
		produces = {"application/json"},
		method = RequestMethod.GET)
	ResponseEntity<List<String>> complete(@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q", required = true) String q) {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("Did you mean this?");
		strings.add("no, you probably meant this");
		strings.add("no?");
		strings.add("then what did you mean? i can't figure out");

		return ResponseEntity.ok(strings);
	}
}
