package com.app.samyang.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.samyang.entity.JyMember;
import com.app.samyang.entity.JyProduct;
import com.app.samyang.entity.JyReview;
import com.app.samyang.service.JyProductService;
import com.app.samyang.service.JyReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/jyreview")
public class JyReviewController {

    private final JyProductService productService;
    private final JyReviewService jyReviewService;

    // ✅ 전체 리뷰 목록
    @GetMapping
    public String reviewList(Model model) {
        List<JyReview> jyReviews = jyReviewService.findAll();
        model.addAttribute("reviews", jyReviews);
        return "product/jyreview"; // 리뷰 목록 페이지
    }

    // ✅ 리뷰 작성 폼
    @GetMapping("/jywrite")
    public String writeForm(Model model, HttpSession session) {
        JyMember member = (JyMember) session.getAttribute("jyloginMember");
        if (member == null) return "redirect:/member/jylogin";

        List<JyProduct> myProducts = productService.findAll().stream()
                .filter(p -> member.getId().equals("admin")) // 추후 본인 작성 상품으로 바꾸면 여기도
                .collect(Collectors.toList());

        model.addAttribute("myProducts", myProducts);
        model.addAttribute("review", new JyReview());
        return "product/jywrite"; // 🔁 등록 페이지 경로 수정
    }

    // ✅ 리뷰 저장 처리
    @PostMapping("/jysave")
    public String save(@ModelAttribute JyReview jyReview,
                       @org.springframework.web.bind.annotation.RequestParam("product.no") Long productNo,
                       HttpSession session) {
        JyMember member = (JyMember) session.getAttribute("jyloginMember");
        if (member == null) return "redirect:/member/jylogin";

        JyProduct product = productService.findById(productNo); // 상품 조회
        jyReview.setProduct(product); // ✅ 반드시 연결
        jyReview.setWriter(member.getId());

        jyReviewService.save(jyReview);
        return "redirect:/jyreview";
    }
    
    @PostMapping("/jydelete/{id}")
    public String deleteReview(@PathVariable("id") Long id, HttpSession session) {
        JyMember member = (JyMember) session.getAttribute("jyloginMember");
        if (member == null) return "redirect:/member/jylogin";

        JyReview jyReview = jyReviewService.findById(id);
        if (jyReview != null && jyReview.getWriter().equals(member.getId())) {
            jyReviewService.delete(id);
        }

        return "redirect:/jyreview";
    }
}
