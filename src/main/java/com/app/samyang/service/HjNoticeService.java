package com.app.samyang.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.HjNotice;
import com.app.samyang.repository.HjNoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HjNoticeService {
    private final HjNoticeRepository hjnoticeRepository;

    public List<HjNotice> getAll() {
        return hjnoticeRepository.findByOrderByResdateDesc();
    }
    
    public HjNotice save(HjNotice notice) {
        return hjnoticeRepository.save(notice);
    }

    public Optional<HjNotice> getById(Long no) {
        return hjnoticeRepository.findById(no);
    }
    
    public HjNotice getHjNotice(Long no) {
        return hjnoticeRepository.findById(no).orElse(null);
    }

    public void delete(Long no) {
        hjnoticeRepository.deleteById(no);
    }
    
    public Page<HjNotice> getNoticeList(Pageable pageable) {
        return hjnoticeRepository.findAll(pageable);
    }
}
