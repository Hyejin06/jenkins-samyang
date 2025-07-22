package com.app.samyang.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.samyang.entity.JyMember;
import com.app.samyang.entity.JyQna;
import com.app.samyang.service.JyQnaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
@CrossOrigin(origins = "*")
public class JyQnaController {

    private final JyQnaService jyQnaService;

    // ✅ 전체 목록
    @GetMapping("/jylist")
    public String getAllQna(Model model) {
        model.addAttribute("qnas", jyQnaService.getAllQna());
        return "qna/jylist";
    }

    // ✅ 질문 + 답변 상세
    @GetMapping("/jydetail/{no}")
    public String getQna(@PathVariable("no") Long no, Model model,
                         HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        boolean isViewed = false;
        String cookieName = "qna_view_" + no;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    isViewed = true;
                    break;
                }
            }
        }

        if (!isViewed) {
            jyQnaService.increaseHits(no);
            Cookie newCookie = new Cookie(cookieName, "viewed");
            newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setPath("/");
            res.addCookie(newCookie);
        }

        model.addAttribute("qna", jyQnaService.findByNo(no));
        return "qna/jydetail";
    }

    // ✅ 질문 등록 폼 (로그인한 회원만)
    @GetMapping("/jyins")
    public String questionForm(Model model, HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null) return "redirect:/member/login";

        JyQna jyQna = new JyQna();
        jyQna.setAuthor(jyMember.getId());
        model.addAttribute("qna", jyQna);
        return "qna/jyform";
    }

    // ✅ 질문 등록 처리
    @PostMapping("/jynew")
    public String saveQuestion(@ModelAttribute JyQna jyQna, HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null) return "redirect:/member/login";

        jyQna.setAuthor(jyMember.getId());
        jyQna.setResdate(LocalDateTime.now());
        jyQnaService.save(jyQna);
        return "redirect:/qna/jylist";
    }

    // ✅ 관리자 답변 폼
    @GetMapping("/jyanswer/{no}")
    public String answerForm(@PathVariable("no") Long no, Model model, HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null || !"admin".equals(jyMember.getId())) return "redirect:/qna/list";

        JyQna jyQna = jyQnaService.findByNo(no);
        model.addAttribute("qna", jyQna);
        return "qna/jyanswer";  // → 답변 작성용 폼
    }

    // ✅ 관리자 답변 등록 처리
    @PostMapping("/jyanswer")
    public String submitAnswer(@RequestParam("no") Long no,
                                @RequestParam("answer") String answer,
                                HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null || !"admin".equals(jyMember.getId())) return "redirect:/qna/list";

        JyQna jyQna = jyQnaService.findByNo(no);
        jyQna.setAnswer(answer);
        jyQnaService.save(jyQna);
        return "redirect:/qna/jydetail/" + no;
    }

    // ✅ 수정 폼 (작성자만)
    @GetMapping("/jyedit/{no}")
    public String editForm(@PathVariable("no") Long no, Model model, HttpSession session) {
        JyQna jyQna = jyQnaService.findByNo(no);
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");

        if (jyMember == null || !jyMember.getId().equals(jyQna.getAuthor())) {
            return "redirect:/qna/jylist";
        }

        model.addAttribute("qna", jyQna);
        return "qna/jyedit";
    }

    // ✅ 수정 처리
    @PostMapping("/jyupdate")
    public String update(@ModelAttribute JyQna jyQna, HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null || !jyMember.getId().equals(jyQna.getAuthor())) {
            return "redirect:/qna/list";
        }

        JyQna original = jyQnaService.findByNo(jyQna.getNo());
        original.setTitle(jyQna.getTitle());
        original.setContent(jyQna.getContent());
        original.setResdate(LocalDateTime.now());
        jyQnaService.save(original);
        return "redirect:/qna/jydetail/" + jyQna.getNo();
    }

    // ✅ 질문 삭제 (작성자만 가능)
    @GetMapping("/jydelete/{no}")
    public String delete(@PathVariable("no") Long no, HttpSession session) {
        JyQna jyQna = jyQnaService.findByNo(no);
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");

        if (jyMember == null || !jyMember.getId().equals(jyQna.getAuthor())) {
            return "redirect:/qna/jylist";
        }

        jyQnaService.delete(no);
        return "redirect:/qna/jylist";
    }
}
