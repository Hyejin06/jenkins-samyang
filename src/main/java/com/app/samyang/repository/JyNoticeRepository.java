package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.JyNotice;

public interface JyNoticeRepository extends JpaRepository<JyNotice, Long> {
	List<JyNotice> findByOrderByResdateDesc();
	List<JyNotice> findTop3ByOrderByResdateDesc();
}
