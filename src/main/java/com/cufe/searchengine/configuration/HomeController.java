package com.cufe.searchengine.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
public class HomeController {

	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	@RequestMapping("/swagger")
	public String swagger() {
		return "redirect:swagger-ui.html";
	}
}
