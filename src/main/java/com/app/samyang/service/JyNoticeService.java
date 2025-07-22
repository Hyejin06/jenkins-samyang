package com.app.samyang.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.samyang.entity.JyNotice;
import com.app.samyang.repository.JyNoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JyNoticeService {
    private final JyNoticeRepository jyNoticeRepository;

    public List<JyNotice> getAll() {
        return jyNoticeRepository.findByOrderByResdateDesc();
    }

    public Page<JyNotice> getNoticeList(Pageable pageable) {
        return jyNoticeRepository.findAll(pageable);
    }

    public JyNotice save(JyNotice jyNotice) {
        return jyNoticeRepository.save(jyNotice);
    }

    public Optional<JyNotice> getById(Long no) {
        return jyNoticeRepository.findById(no);
    }

    public JyNotice getNotice(Long no) {
        return jyNoticeRepository.findById(no).orElse(null);
    }

    public void delete(Long no) {
        jyNoticeRepository.deleteById(no);
    }
}
