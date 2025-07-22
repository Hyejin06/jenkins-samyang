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

import com.app.samyang.entity.HjMember;
import com.app.samyang.entity.HjProduct;
import com.app.samyang.entity.HjReview;
import com.app.samyang.service.HjProductService;
import com.app.samyang.service.HjReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hjreview")
public class HjReviewController {

    private final HjProductService productService;
    private final HjReviewService hjReviewService;

    // ✅ 전체 리뷰 목록
    @GetMapping
    public String reviewList(Model model) {
        List<HjReview> hjReviews = hjReviewService.findAll();
        model.addAttribute("reviews", hjReviews);
        return "product/hjreview"; // 리뷰 목록 페이지
    }

    // ✅ 리뷰 작성 폼
    @GetMapping("/hjwrite")
    public String writeForm(Model model, HttpSession session) {
    	HjMember member = (HjMember) session.getAttribute("hjloginMember");
        if (member == null) return "redirect:/member/hjlogin";

        List<HjProduct> myProducts = productService.findAll().stream()
                .filter(p -> member.getId().equals("admin")) // 추후 본인 작성 상품으로 바꾸면 여기도
                .collect(Collectors.toList());

        model.addAttribute("myProducts", myProducts);
        model.addAttribute("review", new HjReview());
        return "product/hjwrite"; // 🔁 등록 페이지 경로 수정
    }

    // ✅ 리뷰 저장 처리
    @PostMapping("/hjsave")
    public String save(@ModelAttribute HjReview hjReview,
                       @org.springframework.web.bind.annotation.RequestParam("product.no") Long productNo,
                       HttpSession session) {
    	HjMember member = (HjMember) session.getAttribute("hjloginMember");
        if (member == null) return "redirect:/member/hjlogin";

        HjProduct product = productService.findById(productNo); // 상품 조회
        hjReview.setProduct(product); // ✅ 반드시 연결
        hjReview.setWriter(member.getId());

        hjReviewService.save(hjReview);
        return "redirect:/hjreview";
    }
    
    @PostMapping("/hjdelete/{id}")
    public String deleteReview(@PathVariable("id") Long id, HttpSession session) {
    	HjMember member = (HjMember) session.getAttribute("hjloginMember");
        if (member == null) return "redirect:/member/hjlogin";

        HjReview hjReview = hjReviewService.findById(id);
        if (hjReview != null && hjReview.getWriter().equals(member.getId())) {
            hjReviewService.delete(id);
        }

        return "redirect:/hjreview";
    }
}
