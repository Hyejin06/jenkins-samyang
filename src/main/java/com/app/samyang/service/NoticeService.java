package com.app.samyang.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.Notice;
import com.app.samyang.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<Notice> getAll() {
        return noticeRepository.findByOrderByResdateDesc();
    }
    
    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Optional<Notice> getById(Long no) {
        return noticeRepository.findById(no);
    }
    
    public Notice getNotice(Long no) {
        return noticeRepository.findById(no).orElse(null);
    }

    public void delete(Long no) {
        noticeRepository.deleteById(no);
    }
    
    public Page<Notice> getNoticeList(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }
}

