package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ 이거 추가

import com.app.samyang.entity.HjCart;
import com.app.samyang.repository.HjCartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HjCartService {
    private final HjCartRepository repo;

    public List<HjCart> getCartList(String memberId) {
        return repo.findByMemberId(memberId);
    }

    public void addToCart(HjCart hjCart) {
        repo.save(hjCart);
    }

    public void removeFromCart(Long id) {
        repo.deleteById(id);
    }

    @Transactional // ✅ 트랜잭션 붙여주기!
    public void clearCart(String memberId) {
        repo.deleteByMemberId(memberId);
    }

    public List<HjCart> findByIds(List<Long> selectedItems) {
        return repo.findAllById(selectedItems);
    }

    public HjCart findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
