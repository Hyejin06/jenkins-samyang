package com.app.samyang.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.samyang.entity.Cart;
import com.app.samyang.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository repo;

    public List<Cart> getCartList(String memberId) {
        return repo.findByMemberId(memberId);
    }

    public void addToCart(Cart cart) {
        repo.save(cart);
    }

    public void removeFromCart(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void clearCart(String memberId) {
        repo.deleteByMemberId(memberId);
    }

    public List<Cart> findByIds(List<Long> selectedItems) {
        return repo.findAllById(selectedItems);
    }

    public Cart findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
