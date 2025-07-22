package com.app.samyang.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.samyang.entity.Member;
import com.app.samyang.entity.Qna;
import com.app.samyang.service.QnaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
@CrossOrigin(origins = "*") // CORS 허용
public class QnaController {

	private final QnaService qnaService;

	@GetMapping("/list")
	public String getAllQnas(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
	    Page<Qna> qnas = qnaService.getQnaList(PageRequest.of(page, 10));
	    List<Qna> replies = qnaService.findRepliesAll();

	    // 여기 로그 추가해서 확인해봐
	    for (Qna q : qnas.getContent()) {
	        System.out.println("[Q] no=" + q.getNo() + ", title=" + q.getTitle());
	        for (Qna a : replies) {
	            if (a.getParno() != null && a.getParno().equals(q.getNo())) {
	                System.out.println(" └─ [A] parno=" + a.getParno() + ", content=" + a.getContent());
	            }
	        }
	    }

	    model.addAttribute("qnas", qnas);
	    model.addAttribute("replies", replies);
	    model.addAttribute("currentPage", page);
	    return "qna/list";
	}
	
	@GetMapping("/detail/{no}") // 답글 상세보기
	public String getQna(@PathVariable("no") Long no, Model model, HttpServletRequest req, HttpServletResponse res) {

		Cookie[] cookies = req.getCookies(); // 사용자 컴퓨터의 쿠키 선언
		boolean isViewed = false;
		String cookieName = "qna_view_" + no;

		if (cookies != null) { // 사용자 컴퓨터의 쿠키를 순회하여 조회
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					isViewed = true;
					break;
				}
			}
		}

		// 조회한 적 없다고 판단된 경우 조회수 증가 및 쿠키 추가
		if (!isViewed) {
			qnaService.increaseHits(no);
			Cookie newCookie = new Cookie(cookieName, "viewed"); // 쿠키 생성
			newCookie.setMaxAge(60 * 60 * 24); // 24시간
			newCookie.setPath("/");
			res.addCookie(newCookie);
		}

		model.addAttribute("qna", qnaService.findByNo(no));
		model.addAttribute("replies", qnaService.findReplies(no));
		return "qna/detail";
	}

	@GetMapping("/question/{parno}") // 질문글 상세보기
	public String getQuestion(@PathVariable("parno") Long parno, Model model) {
		model.addAttribute("qnas", qnaService.findReplies(parno));
		return "qna/detail";
	}

	@GetMapping("/ins") // 질문 폼 로딩
	public String QnaForm(Model model, HttpSession session) {
		Qna qna = new Qna();
		Member Member = (Member) session.getAttribute("loginMember");
		qna.setAuthor(Member.getId());
		model.addAttribute("qna", qna);
		return "qna/form";
	}

	@GetMapping("/reply/ins/{no}") // 답변 폼 로딩
	public String replyForm(@PathVariable("no") Long no, Model model, HttpSession session) {
		Qna qna = new Qna();
		Member Member = (Member) session.getAttribute("loginMember");
		qna.setParno(no);
		qna.setAuthor(Member.getId());
		model.addAttribute("qna", qna);
		return "qna/reply";
	}

	@PostMapping("/new") // 질문 글 쓰기
	public String insQuestion(@ModelAttribute Qna qna, HttpSession session) {
		Member Member = (Member) session.getAttribute("loginMember");

		if (Member == null) {
			return "redirect:/member/login"; // 로그인 안 돼있으면 로그인 페이지로
		}

		qna.setAuthor(Member.getId()); // ✅ 작성자 자동 주입
		qnaService.save(qna);
		return "redirect:/qna/list"; // or /qna/list
	}

	@PostMapping("/reply")
	public String insReply(@ModelAttribute Qna qna, @RequestParam(value = "type", required = false) String type,
	                       HttpSession session) {
	    Member Member = (Member) session.getAttribute("loginMember");

	    if (Member == null) {
	        return "redirect:/member/login";
	    }

	    qna.setAuthor(Member.getId());
	    qna.setLevel(2);

	    // ✅ 답글에도 category 세팅해줘야 함
	    if (type != null && !type.isBlank()) {
	        qna.setCategory(type);
	    }

	    // ✅ 제목도 없으면 기본값 세팅 (이전 문제 해결용)
	    if (qna.getTitle() == null || qna.getTitle().isBlank()) {
	        qna.setTitle("답변");
	    }

	    qnaService.save(qna);

	    return "redirect:/qna/list";
	}
	
	@GetMapping("/edit/{no}") // 글 수정 폼 로딩
	public String updateForm(@PathVariable("no") Long no, Model model) {
		model.addAttribute("qna", qnaService.findByNo(no));
		return "qna/edit";
	}

	@PostMapping("/update") // 글 수정 처리
	public String update(@ModelAttribute Qna qna) {
	    Qna original = qnaService.findByNo(qna.getNo());

	    // 기존 값 유지
	    qna.setParno(original.getParno());
	    qna.setCategory(original.getCategory());
	    qna.setLevel(original.getLevel());

	    qnaService.update(qna);
		return "redirect:/qna/list";
	}

	@GetMapping("/reply/del/{no}") // 해당 답글 삭제
	public String deleteReply(@PathVariable("no") Long no) {
		qnaService.delete(no);
		return "redirect:/qna/list";
	}

	@GetMapping("/delete/{parno}") // 질문 삭제시 해당 답글도 연쇄 삭제
	public String deleteQuestion(@PathVariable("parno") Long parno) {
		qnaService.deleteByParno(parno);
		return "redirect:/qna/list";
	}

	@GetMapping("/faq")
	public String getFaqByCategoryPaged(@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", defaultValue = "0") int page, Model model) {

		if (type == null) {
			return "redirect:/qna/faq?type=product&page=0";
		}

		Page<Qna> qnas = qnaService.findByCategoryPaged(type, PageRequest.of(page, 10));

		model.addAttribute("qnas", qnas); // ✅ Page 객체
		model.addAttribute("selectedType", type);
		model.addAttribute("currentPage", page);

		model.addAttribute("replies", qnaService.findRepliesAll());
		
		return "qna/list";
	}
	
	
}

