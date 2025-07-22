package com.app.samyang.controller;

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

import com.app.samyang.entity.HjMember;
import com.app.samyang.entity.HjQna;
import com.app.samyang.service.HjQnaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
@CrossOrigin(origins = "*") // CORS 허용
public class HjQnaController {

	private final HjQnaService hjqnaService;

	@GetMapping("/hjlist")
	public String getAllQna(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
	    Page<HjQna> qnas = hjqnaService.getAllQuestions(PageRequest.of(page, 10));
	    model.addAttribute("qnas", qnas);
	    return "qna/hjlist";
	}

	@GetMapping("/hjdetail/{no}")
	public String getHjQna(@PathVariable("no") Long no, Model model, HttpServletRequest req, HttpServletResponse res) {
		HjQna target = hjqnaService.findByNo(no);
		HjQna question = target.getLevel() == 1 ? target : hjqnaService.findByNo(target.getParno());

		// 조회수 증가는 질문 기준으로만
		String cookieName = "qna_view_" + question.getNo();
		boolean isViewed = false;

		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					isViewed = true;
					break;
				}
			}
		}

		if (!isViewed) {
			hjqnaService.increaseHits(question.getNo());
			Cookie newCookie = new Cookie(cookieName, "viewed");
			newCookie.setMaxAge(60 * 60 * 24);
			newCookie.setPath("/");
			res.addCookie(newCookie);
		}

		model.addAttribute("qna", question);
		model.addAttribute("replies", hjqnaService.findReplies(question.getNo()));
		return "qna/hjdetail";
	}

	@GetMapping("/hjins") // 질문 폼 로딩
	public String QnaForm(Model model, HttpSession session) {
		HjQna qna = new HjQna();
		HjMember Member = (HjMember) session.getAttribute("hjloginMember");
		qna.setAuthor(Member.getId());
		model.addAttribute("qna", qna);
		return "qna/hjform";
	}

	@GetMapping("/hjreply/ins/{no}")
	public String replyForm(@PathVariable("no") Long no, Model model, HttpSession session) {
		HjQna qna = new HjQna();
		HjMember Member = (HjMember) session.getAttribute("hjloginMember");

		qna.setParno(no);
		qna.setAuthor(Member.getId());
		qna.setLevel(2);
		qna.setContent(""); // ✅ 이 줄을 추가!

		model.addAttribute("qna", qna);
		return "qna/hjreply";
	}
	
	@PostMapping("/hjnew")
	public String insQuestion(@ModelAttribute HjQna qna, HttpSession session) {
		HjMember Member = (HjMember) session.getAttribute("hjloginMember");
		if (Member != null) {
			qna.setAuthor(Member.getId()); // 작성자 설정!
		} else {
			return "redirect:/member/hjlogin"; // 로그인 안 된 경우 처리
		}

		hjqnaService.save(qna);
		return "redirect:/qna/hjlist";
	}

	@PostMapping("/hjreply")
	public String insReply(@ModelAttribute HjQna qna, HttpSession session) {
		qna.setLevel(2);

		HjMember Member = (HjMember) session.getAttribute("hjloginMember");
		if (Member == null) {
			return "redirect:/member/hjlogin";
		}
		qna.setAuthor(Member.getId());

		// ✅ reply에는 title이 필요 없지만 null이면 에러나므로 빈 문자열이라도 넣어줌
		if (qna.getTitle() == null || qna.getTitle().isBlank()) {
			qna.setTitle("답변");
		}

		hjqnaService.save(qna);
		return "redirect:/qna/hjdetail/" + qna.getParno();
	}

	@GetMapping("/hjedit/{no}") // 글 수정 폼 로딩
	public String updateForm(@PathVariable("no") Long no, Model model) {
		model.addAttribute("qna", hjqnaService.findByNo(no));
		return "qna/hjedit";
	}

	@PostMapping("/hjupdate")
	public String update(@ModelAttribute HjQna qna) {
	    HjQna original = hjqnaService.findByNo(qna.getNo()); // 📌 여기서 진짜 DB에 있는 기존 글 가져옴

	    // 👉 필요한 필드만 수정하고, 나머지는 유지
	    original.setTitle(qna.getTitle());
	    original.setContent(qna.getContent());

	    hjqnaService.update(original); // 📌 기존 글 덮어쓰기 아님! 정확히 필요한 필드만 변경해서 저장
	    return "redirect:/qna/hjdetail/" + qna.getNo();
	}

	@GetMapping("/hjreply/del/{no}") // 해당 답글 삭제
	public String deleteReply(@PathVariable("no") Long no) {
		hjqnaService.delete(no);
		return "redirect:/qna/hjlist";
	}

	@GetMapping("/hjdelete/{parno}") // 질문 삭제시 해당 답글도 연쇄 삭제
	public String deleteQuestion(@PathVariable("parno") Long parno) {
		hjqnaService.deleteByParno(parno);
		return "redirect:/qna/hjlist";
	}
}
