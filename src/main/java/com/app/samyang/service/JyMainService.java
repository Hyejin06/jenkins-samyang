package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.samyang.entity.JyProduct;
import com.app.samyang.entity.JyQna;
import com.app.samyang.repository.JyProductRepository;
import com.app.samyang.repository.JyQnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JyMainService {
	private final JyProductRepository jyProductRepository;
	private final JyQnaRepository jyQnaRepository;
	
	public List<JyProduct> getLatestProducts() {
	    return jyProductRepository.findTop4ByOrderByNoDesc();  // 최근 상품 4개
	}
	
	public List<JyQna> getLatestQnas() {
		return jyQnaRepository.findTop3ByLevelOrderByResdateDesc(1);
	}
	
}

