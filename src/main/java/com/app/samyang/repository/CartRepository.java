package com.app.samyang.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.samyang.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByMemberId(String memberId);
    void deleteByMemberId(String memberId);
}
