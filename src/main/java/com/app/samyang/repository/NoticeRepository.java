package com.app.samyang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.samyang.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	List<Notice> findByOrderByResdateDesc();
	List<Notice> findTop3ByOrderByResdateDesc();
}
