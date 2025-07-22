package com.app.samyang.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.samyang.entity.HjNotice;
import com.app.samyang.service.HjNoticeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class HjNoticeController {
    private final HjNoticeService hjnoticeService;

    @GetMapping("/hjins")
    public String form(Model model) {
        model.addAttribute("notice", new HjNotice());
        return "notice/hjform";
    }

    @PostMapping("/hjsave")
    public String save(@ModelAttribute HjNotice notice) {
        hjnoticeService.save(notice);
        return "redirect:/notice/hjlist";
    }

    @GetMapping("/hjedit/{no}")
    public String edit(@PathVariable("no") Long no, Model model) {
    	HjNotice notice = hjnoticeService.getById(no).orElseThrow();
        model.addAttribute("notice", notice);
        return "notice/hjedit";
    }

    @PostMapping("/hjupdate")
    public String update(@ModelAttribute HjNotice notice) {
        hjnoticeService.save(notice);
        return "redirect:/notice/hjlist";
    }

    @GetMapping("/hjdetail/{no}")
    public String detail(@PathVariable("no") Long no, Model model) {
    	HjNotice notice = hjnoticeService.getById(no).orElseThrow();
        notice.setHits(notice.getHits() + 1);
        hjnoticeService.save(notice);
        model.addAttribute("notice", notice);
        return "notice/hjdetail";
    }

    @GetMapping("/hjdelete/{no}")
    public String delete(@PathVariable("no") Long no) {
        hjnoticeService.delete(no);
        return "redirect:/notice/hjlist";
    }
    
    @GetMapping("/hjlist")
    public String getAllNotices(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<HjNotice> notices = hjnoticeService.getNoticeList(PageRequest.of(page, 10, Sort.by("no").descending()));

        model.addAttribute("notices", notices); 
        return "notice/hjlist";
    }
}