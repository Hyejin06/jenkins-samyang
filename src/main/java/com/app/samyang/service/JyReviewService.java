package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.JyProduct;
import com.app.samyang.entity.JyReview;
import com.app.samyang.repository.JyProductRepository;
import com.app.samyang.repository.JyReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JyReviewService {

	private final JyProductRepository productRepo;
	private final JyReviewRepository repo;

	// 리뷰 등록
	public void save(JyReview jyReview) {
	    repo.save(jyReview);

	    JyProduct product = jyReview.getProduct();
	    if (product != null) {
	        // 해당 상품의 모든 리뷰 다시 조회해서 평균 계산
	        List<JyReview> jyReviews = repo.findByProductNo(product.getNo());

	        int totalRating = jyReviews.stream().mapToInt(JyReview::getRating).sum();
	        int reviewCount = jyReviews.size();
	        int avgRating = (int) Math.round((double) totalRating / reviewCount);

	        product.setRating(avgRating);
	        product.setReviewCount(reviewCount);

	        // 상품 저장
	        productRepo.save(product);  // productRepo 필요함!
	    }
	}


	// 특정 상품의 리뷰 조회
	public List<JyReview> findByProduct(Long productNo) {
		return repo.findByProductNo(productNo);
	}

	// ✅ 전체 리뷰 조회
	public List<JyReview> findAll() {
	    return repo.findAllWithProduct();  // 🚀 함께 로딩!
	}

	public JyReview findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	public void delete(Long id) {
	    JyReview jyReview = repo.findById(id).orElse(null);

	    if (jyReview != null) {
	        JyProduct product = jyReview.getProduct(); // 리뷰와 연결된 상품
	        repo.deleteById(id); // 리뷰 삭제 먼저

	        if (product != null) {
	            List<JyReview> jyReviews = repo.findByProductNo(product.getNo()); // 삭제 후 남은 리뷰들

	            int reviewCount = jyReviews.size();
	            int avgRating = 0;

	            if (reviewCount > 0) {
	                int totalRating = jyReviews.stream().mapToInt(JyReview::getRating).sum();
	                avgRating = (int) Math.round((double) totalRating / reviewCount);
	            }

	            product.setRating(avgRating);
	            product.setReviewCount(reviewCount);
	            productRepo.save(product);
	        }
	    }
	}
}
