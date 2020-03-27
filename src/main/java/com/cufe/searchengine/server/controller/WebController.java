package com.cufe.searchengine.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	@RequestMapping("/search")
	public String search() {
		return "redirect:/";
	}

	@RequestMapping("/doc")
	public String doc() {
		return "redirect:swagger-ui.html";
	}
}
