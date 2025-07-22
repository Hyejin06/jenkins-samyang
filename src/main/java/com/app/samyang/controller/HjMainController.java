package com.app.samyang.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.samyang.service.HjMainService;
import com.app.samyang.service.HjNoticeService;
import com.app.samyang.service.HjQnaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HjMainController {

	private final HjMainService hjmainService;
	private final HjNoticeService hjNoticeService;
	private final HjQnaService hjQnaService;

	@GetMapping("/hj")
	public String hj(Model model) {
		model.addAttribute("hjQnas", hjQnaService.getLatestQnas());
		return "hj";
	}

	@GetMapping("/hjcenter")
	public String hjcenter(Model model) {
		Pageable pageable = PageRequest.of(0, 5, Sort.by("no").descending());
		model.addAttribute("hjNotices", hjNoticeService.getNoticeList(pageable));
		model.addAttribute("hjQnas", hjQnaService.getQnaList(pageable));
		return "hjcenter";
	}
}