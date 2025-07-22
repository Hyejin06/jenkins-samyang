package com.app.samyang.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.HjNotice;
import com.app.samyang.entity.HjProduct;
import com.app.samyang.entity.HjQna;
import com.app.samyang.repository.HjNoticeRepository;
import com.app.samyang.repository.HjProductRepository;
import com.app.samyang.repository.HjQnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HjMainService {
	private final HjProductRepository hjproductRepository;
	private final HjNoticeRepository hjNoticeRepository;
	private final HjQnaRepository hjQnaRepository;

	public List<HjQna> getLatestQnas() {
		return hjQnaRepository.findTop3ByOrderByResdateDesc(); // 예시
	}

	public List<HjNotice> getLatestNotices() {
		return hjNoticeRepository.findTop3ByOrderByResdateDesc(); // 예시
	}

}
