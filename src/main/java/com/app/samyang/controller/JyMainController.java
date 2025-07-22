package com.app.samyang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.samyang.service.JyMainService;
import com.app.samyang.service.JyProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JyMainController {

	private final JyMainService jyMainService;
	private final JyProductService jyProductService;

	@GetMapping("/jy")
	public String index(Model model) {
		model.addAttribute("products", jyMainService.getLatestProducts());
		model.addAttribute("qnas", jyMainService.getLatestQnas());
		model.addAttribute("bestProducts", jyProductService.findTop4ByCate("BEST"));
		model.addAttribute("newProducts", jyProductService.findTop4ByCate("NEW"));
		return "jy";
	}
}
