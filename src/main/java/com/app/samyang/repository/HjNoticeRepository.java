package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.HjNotice;

public interface HjNoticeRepository extends JpaRepository<HjNotice, Long> {
	List<HjNotice> findByOrderByResdateDesc();
	List<HjNotice> findTop3ByOrderByResdateDesc();
}
