package com.app.samyang.controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.samyang.entity.JyProduct;
import com.app.samyang.service.JyProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class JyProductController {

	private final JyProductService service;

	@GetMapping("/jylist")
	public String list(Model model) {
		model.addAttribute("products", service.findAll());
		return "product/jylist";
	}

	@GetMapping("/jyform")
	public String form(Model model) {
		File dir = new File("src/main/resources/static/img/jy");
		String[] fileList = dir.list((d, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
		List<String> files = Arrays.stream(fileList)
			.map(name -> "/img/jy/" + name)
			.collect(Collectors.toList());

		model.addAttribute("product", new JyProduct());
		model.addAttribute("imgFiles", files);
		return "product/jyform";
	}

	@PostMapping("/jysave")
	public String save(@ModelAttribute JyProduct jyProduct) {
		// 파일 업로드는 제거, img1 값만 그대로 사용
		service.save(jyProduct);
		return "redirect:/product/jylist";
	}

	@GetMapping("/jydetail/{no}")
	public String detail(@PathVariable("no") Long no, Model model) {
		model.addAttribute("product", service.findById(no));
		return "product/jydetail";
	}

	@PostMapping("/jydelete/{no}")
	public String deletePost(@PathVariable("no") Long no) {
		service.delete(no);
		return "redirect:/product/jylist";
	}

}
