package com.app.samyang.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.app.samyang.entity.Product;
import com.app.samyang.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("product")
public class ProductController {

	private final ProductService productService;

	@GetMapping("/list")
	public String getCateOrAllList(@RequestParam(name = "category", defaultValue = "ramyun") String category,
			Model model) {
		model.addAttribute("products", productService.findByCate(category));
		model.addAttribute("category", category);
		return "product/list";
	}

	@GetMapping("/list/{cate}") // 카테고리 상품 목록
	public String getCateList(@PathVariable("cate") String cate, Model model) {
		model.addAttribute("products", productService.findByCate(cate));
		model.addAttribute("cate", cate);
		return "product/cate-list";
	}

	@GetMapping("/detail/{no}") // 한 건의 상품 정보
	public String getProduct(@PathVariable("no") Long no, Model model) {
		model.addAttribute("product", productService.findById(no));
		return "product/detail";
	}

	@GetMapping("/ins") // 상품 등록 폼 로딩
	public String productForm(Model model) {
		model.addAttribute("product", new Product());
		return "product/form";
	}

	@PostMapping("/new")
	public String insProduct(@ModelAttribute Product product, @RequestParam("file1") MultipartFile file1) throws IOException {
		String uploadDir = "/uploadtest/";
		File dir = new File(uploadDir);
		if (!dir.exists()) dir.mkdirs();

		if (!file1.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + file1.getOriginalFilename();
			file1.transferTo(new File(uploadDir + fileName));
			product.setImg1("/uploadtest/" + fileName); // thymeleaf용 상대경로
		}

		productService.save(product);
		return "redirect:/product/list";
	}

	@GetMapping("/edit/{no}") // 상품 정보 수정 폼 로딩
	public String editForm(@PathVariable("no") Long no, Model model) {
		model.addAttribute("product", productService.findById(no));
		return "product/edit";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute Product product, @RequestParam("file1") MultipartFile file1,
			HttpServletRequest request) throws IOException {

		String uploadPath = request.getServletContext().getRealPath("/img/");
		File dir = new File(uploadPath);
		if (!dir.exists())
			dir.mkdirs();

		if (!file1.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + file1.getOriginalFilename();
			file1.transferTo(new File(uploadPath + fileName));
			product.setImg1("/img/" + fileName);
		}

		productService.save(product);
		return "redirect:/product/list";
	}

	@GetMapping("/delete/{no}")
	public String delete(@PathVariable("no") Long no) {
		productService.delete(no);
		return "redirect:/product/list";
	}

}
