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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    ResponseEntity<ResultPage> imageQuery(@NotNull @ApiParam(value = "string to search for", required = true) @Valid @RequestParam(value = "q", required = true) String q,@ApiParam(value = "page of results to fetch, default 1") @Valid @RequestParam(value = "page", required = false) Integer page, HttpServletRequest request) {
		if ("1".equals(System.getenv("MOCK"))) {
			ArrayList<QueryResult> queryResults = new ArrayList<>();
			queryResults.add(new QueryResult()
					.title("Child Portrait Artist | Family Portraits | Custom Child Painitngs ...")
					.link("https://kzart.com/wp-content/uploads/2019/05/Child-Portrait-Artist-Surrey-1-a.jpg")
			);
			queryResults.add(new QueryResult()
					.title("Child Portrait Artist | Family Portraits | Custom Child Painitngs ...")
					.link("https://kzart.com/wp-content/uploads/2019/05/Child-Portrait-Artist-Surrey-7-a.jpg")
			);
			queryResults.add(new QueryResult()
					.title("What is Butterfly Lighting and How to Use it in Portrait ...")
					.link("https://bidunart.com/wp-content/uploads/2020/01/Portrait382-1280x640.jpg")
			);
			queryResults.add(new QueryResult()
					.title("How to Do Hard Light Portraits | Profoto (CN)")
					.link("https://cdn.profoto.com/cdn/0521660/contentassets/b61cf60b567f46ac9a274c3f87a5bcb1/portrait_technique_0014.jpg?width=1280&quality=75&format=jpg")
			);
			queryResults.add(new QueryResult()
					.title("What is Fine Art Portrait Photography? | Bidun Art")
					.link("https://bidunart.com/wp-content/uploads/2019/11/Femaleportraits357a-1-1280x640.jpg")
			);
			queryResults.add(new QueryResult()
					.title("15 Most Beautiful Cars Available Today - Best-Looking 2020 Models")
					.link("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/mostbeauty-1584578506.jpg")
			);
			queryResults.add(new QueryResult()
					.title("Disney Cars")
					.link("https://lumiere-a.akamaihd.net/v1/images/r_carsfranchise_cars3_postbluray_mobile_10d32ee1.jpeg")
			);
			queryResults.add(new QueryResult()
					.title("Class of 2021: The New and Redesigned Cars, Trucks and SUVs ...")
					.link("https://www.kbb.com/articles/wp-content/uploads/2019/12/2021-ford-mustang-mach-e-front-16-9-700x500.jpg")
			);
			queryResults.add(new QueryResult()
					.title("Audi RS6 Avant/RS7")
					.link("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/2020-audi-rs7-112-1569274021.jpg")
			);
			queryResults.add(new QueryResult()
					.title("Aston Martin DBS Superleggera")
					.link("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/2019-aston-martin-dbs-superleggera-mmp-1545071883.jpg")
			);

			return ResponseEntity.ok(new ResultPage().currentPage(1).totalPages(10).results(queryResults));
		} else {
			// TODO: add isImage
			List<QueryResult> queryResults = queryProcessor.search(q, request.getRemoteAddr());

			int pages = (int) Math.ceil(queryResults.size() / 10.0d);

			if (page > pages) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			List<QueryResult> subList = queryResults.subList((page - 1) * 10, Math.min(page * 10, queryResults.size()));

			return ResponseEntity.ok(new ResultPage().currentPage(page).totalPages(pages).results(subList));
		}
    }
}
