package com.app.samyang.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.app.samyang.entity.HjProduct;
import com.app.samyang.service.HjProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("product")
public class HjProductController {

	private final HjProductService productService;

	// 전체 상품 목록
	@GetMapping("/hjlist")
	public String getAllOrCateList(@RequestParam(name = "cate", required = false, defaultValue = "전체") String cate, Model model) {
		if (cate.equals("전체")) {
			model.addAttribute("products", productService.findAll());
		} else {
			model.addAttribute("products", productService.findByCate(cate));
		}
		model.addAttribute("cate", cate);
		return "product/hjlist"; // 기존 리스트 템플릿 사용
	}

	// 카테고리별 상품 목록
	@GetMapping("/hjlist/{cate}")
	public String getCateList(@PathVariable("cate") String cate, Model model) {
		model.addAttribute("products", productService.findByCate(cate));
		model.addAttribute("cate", cate);
		return "product/hjcate-list";
	}

	// 상품 상세보기
	@GetMapping("/hjdetail/{no}")
	public String getProduct(@PathVariable("no") Long no, Model model) {
		model.addAttribute("product", productService.findById(no));
		return "product/hjdetail";
	}

	// 상품 등록 폼
	@GetMapping("/hjins")
	public String productForm(Model model) {
		model.addAttribute("product", new HjProduct());
		return "product/hjform";
	}

	// 상품 등록 처리
	@PostMapping("/hjnew")
	public String insProduct(@ModelAttribute HjProduct product,
	                         @RequestParam("file1") MultipartFile file1,
	                         @RequestParam(value = "statusImgFile", required = false) MultipartFile statusImgFile,
	                         HttpServletRequest request) throws IOException {
	    String uploadPath = request.getServletContext().getRealPath("/img/");
	    File dir = new File(uploadPath);
	    if (!dir.exists()) dir.mkdirs();

	    if (!file1.isEmpty()) {
	        String fileName = UUID.randomUUID() + "_" + file1.getOriginalFilename();
	        file1.transferTo(new File(uploadPath + fileName));
	        product.setImg1("/img/" + fileName);
	    }

	    if (statusImgFile != null && !statusImgFile.isEmpty()) {
	        String fileName = UUID.randomUUID() + "_" + statusImgFile.getOriginalFilename();
	        statusImgFile.transferTo(new File(uploadPath + fileName));
	        product.setStatusImg("/img/" + fileName); // 이건 엔티티 필드 그대로
	    }

	    productService.save(product);
	    return "redirect:/product/hjlist";
	}

	// 상품 수정 폼
	@GetMapping("/hjedit/{no}")
	public String editForm(@PathVariable("no") Long no, Model model) {
		model.addAttribute("product", productService.findById(no));
		return "product/hjedit";
	}

	// 상품 수정 처리
	@PostMapping("/hjupdate")
	public String update(@ModelAttribute HjProduct product,
						 @RequestParam("file1") MultipartFile file1,
						 @RequestParam(name = "statusImg", required = false) MultipartFile statusImg,
						 HttpServletRequest request) throws IOException {
		return insProduct(product, file1, statusImg, request);
	}

	// 상품 삭제
	@GetMapping("/hjdelete/{no}")
	public String delete(@PathVariable("no") Long no) {
		productService.delete(no);
		return "redirect:/product/hjlist";
	}
}