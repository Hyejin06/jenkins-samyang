package com.app.samyang.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.app.samyang.entity.Member;
import com.app.samyang.entity.Product;
import com.app.samyang.entity.Review;
import com.app.samyang.service.ProductService;
import com.app.samyang.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ProductService productService;
    private final ReviewService reviewService;

    // ✅ 전체 리뷰 목록
    @GetMapping
    public String reviewList(Model model) {
        List<Review> reviews = reviewService.findAll();
        model.addAttribute("reviews", reviews);
        return "product/review"; // 리뷰 목록 페이지
    }

    // ✅ 리뷰 작성 폼
    @GetMapping("/write")
    public String writeForm(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) return "redirect:/member/login";

        // ✅ 임시로 모든 사용자에게 전체 상품 보여주기
        List<Product> myProducts = productService.findAll();

        model.addAttribute("myProducts", myProducts);
        model.addAttribute("review", new Review());
        return "product/write";
    }

    // ✅ 리뷰 저장 처리
    @PostMapping("/save")
    public String save(@ModelAttribute Review review,
                       @RequestParam("product.no") Long productNo,
                       HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) return "redirect:/member/login";

        Product product = productService.findById(productNo);
        review.setProduct(product);
        review.setWriter(member.getId());

        reviewService.save(review);
        return "redirect:/review";
    }

    // ✅ 리뷰 삭제
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) return "redirect:/member/login";

        Review review = reviewService.findById(id);
        if (review != null && review.getWriter().equals(member.getId())) {
            reviewService.delete(id);
        }

        return "redirect:/review";
    }
}
