package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.HjProduct;
import com.app.samyang.entity.HjReview;
import com.app.samyang.entity.HjProduct;
import com.app.samyang.entity.HjReview;
import com.app.samyang.repository.HjProductRepository;
import com.app.samyang.repository.HjReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HjReviewService {

	private final HjProductRepository productRepo;
	private final HjReviewRepository repo;

	// 리뷰 등록
	public void save(HjReview hjReview) {
	    repo.save(hjReview);

	    HjProduct product = hjReview.getProduct();
	    if (product != null) {
	        // 해당 상품의 모든 리뷰 다시 조회해서 평균 계산
	        List<HjReview> hjReviews = repo.findByProductNo(product.getNo());

	        int totalRating = hjReviews.stream().mapToInt(HjReview::getRating).sum();
	        int reviewCount = hjReviews.size();
	        int avgRating = (int) Math.round((double) totalRating / reviewCount);

	        product.setRating(avgRating);
	        product.setReviewCount(reviewCount);

	        // 상품 저장
	        productRepo.save(product);  // productRepo 필요함!
	    }
	}


	// 특정 상품의 리뷰 조회
	public List<HjReview> findByProduct(Long productNo) {
		return repo.findByProductNo(productNo);
	}

	// ✅ 전체 리뷰 조회
	public List<HjReview> findAll() {
	    return repo.findAllWithProduct();  // 🚀 함께 로딩!
	}

	public HjReview findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	public void delete(Long id) {
		HjReview hjReview = repo.findById(id).orElse(null);

	    if (hjReview != null) {
	    	HjProduct product = hjReview.getProduct(); // 리뷰와 연결된 상품
	        repo.deleteById(id); // 리뷰 삭제 먼저

	        if (product != null) {
	            List<HjReview> hjReviews = repo.findByProductNo(product.getNo()); // 삭제 후 남은 리뷰들

	            int reviewCount = hjReviews.size();
	            int avgRating = 0;

	            if (reviewCount > 0) {
	                int totalRating = hjReviews.stream().mapToInt(HjReview::getRating).sum();
	                avgRating = (int) Math.round((double) totalRating / reviewCount);
	            }

	            product.setRating(avgRating);
	            product.setReviewCount(reviewCount);
	            productRepo.save(product);
	        }
	    }
	}
}
