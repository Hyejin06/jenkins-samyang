package com.app.samyang.controller;

import java.time.LocalDateTime;

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

import com.app.samyang.entity.JyNotice;
import com.app.samyang.service.JyNoticeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class JyNoticeController {
    private final JyNoticeService jyNoticeService;

    @GetMapping("/jylist")
    public String getAllNotices(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<JyNotice> jyNotices = jyNoticeService.getNoticeList(PageRequest.of(page, 10, Sort.by("no").descending()));
        model.addAttribute("noticeList", jyNotices.getContent());
        model.addAttribute("page", jyNotices);
        return "notice/jylist";
    }

    @GetMapping("/jyins")
    public String form(Model model) {
        model.addAttribute("notice", new JyNotice());
        return "notice/jyform";
    }

    @PostMapping("/jysave")
    public String save(@ModelAttribute JyNotice jyNotice) {
        jyNoticeService.save(jyNotice);
        return "redirect:/notice/jylist";
    }

    @GetMapping("/jyedit/{no}")
    public String edit(@PathVariable("no") Long no, Model model) {
        JyNotice jyNotice = jyNoticeService.getById(no).orElseThrow();
        model.addAttribute("notice", jyNotice);
        return "notice/jyedit";
    }

    @PostMapping("/jyupdate")
    public String update(@ModelAttribute JyNotice jyNotice) {
        JyNotice original = jyNoticeService.getById(jyNotice.getNo()).orElseThrow();

        // ✅ 작성자나 조회수 등 유지할 값 복사
        jyNotice.setHits(original.getHits());

        // ✅ 수정한 시간으로 변경
        jyNotice.setResdate(LocalDateTime.now());

        jyNoticeService.save(jyNotice);
        return "redirect:/notice/jylist";
    }

    @GetMapping("/jydetail/{no}")
    public String detail(@PathVariable("no") Long no, Model model) {
        JyNotice jyNotice = jyNoticeService.getById(no).orElseThrow();
        jyNotice.setHits(jyNotice.getHits() + 1);
        jyNoticeService.save(jyNotice);
        model.addAttribute("notice", jyNotice);
        return "notice/jydetail";
    }

    @GetMapping("/jydelete/{no}")
    public String delete(@PathVariable("no") Long no) {
        jyNoticeService.delete(no);
        return "redirect:/notice/jylist";
    }
}
