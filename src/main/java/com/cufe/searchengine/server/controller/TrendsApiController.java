package com.cufe.searchengine.server.controller;

import com.cufe.searchengine.query.TrendsHandler;
import com.cufe.searchengine.server.model.Person;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
@Api(value = "trends", description = "the trends API")
public class TrendsApiController {
	private final NativeWebRequest request;
	private final TrendsHandler trendsHandler;

	@Autowired
	public TrendsApiController(NativeWebRequest request, TrendsHandler trendsHandler) {
		this.request = request;
		this.trendsHandler = trendsHandler;
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
	@ApiOperation(value = "get list of 10 most searched persons of given country.", nickname = "trends", notes = "", response = Person.class, responseContainer = "List", tags={  })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation, result mustn't be empty", response = Person.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "invalid country code") })
	@RequestMapping(value = "/api/trends",
			produces = { "application/json" },
			method = RequestMethod.GET)
    ResponseEntity<List<Person>> trends(@NotNull @ApiParam(value = "country alpha-3 code (ISO 3166) all capital", required = true) @Valid @RequestParam(value = "country", required = true) String country) {
		if ("1".equals(System.getenv("MOCK"))) {
			ArrayList<Person> persons = new ArrayList<>();
			persons.add(new Person().name("Mahmoud Adas").number(6000000));
			persons.add(new Person().name("Mahmoud Othman Adas").number(90));
			persons.add(new Person().name("Shrek").number(80));
			persons.add(new Person().name("Mahmoud Adas Again").number(7));
			persons.add(new Person().name("Maybe Mahmoud Osman Adas").number(6));
			persons.add(new Person().name("Adas Adas").number(5));
			persons.add(new Person().name("Yup, it's 3ds ᕕ( ᐛ )ᕗ").number(4));
			persons.add(new Person().name("Of course adas is the most trndy, what did you expect?").number(4));
			persons.add(new Person().name("No not adas again").number(4));
			persons.add(new Person().name("Adas (✿\u2060´ ꒳ ` )").number(1));

			return ResponseEntity.ok(persons);
		} else {
			return ResponseEntity.ok(trendsHandler.getTrends(country));
		}
    }
}
