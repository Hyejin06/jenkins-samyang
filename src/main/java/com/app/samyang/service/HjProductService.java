package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.HjProduct;
import com.app.samyang.repository.HjProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HjProductService {
	private final HjProductRepository productRepository;
	
	public List<HjProduct> findAll(){  	//제품목록
		return productRepository.findAll();
	}
	
	public List<HjProduct> findByCate(String cate){
		return productRepository.findByCate(cate);
	}
	
	public HjProduct findById(Long no) {  	//제품 1건 조회(제품 상세보기)
		return productRepository.findById(no).orElse(null);
	}
	
	public HjProduct save(HjProduct product) {  	//제품 등록
		return productRepository.save(product);
	}
	
	public HjProduct update(HjProduct product) { 	//제품 변경
		return productRepository.save(product);
	}
	
	public void delete(Long no) {  	//제품 삭제
		productRepository.deleteById(no);
	}
}
