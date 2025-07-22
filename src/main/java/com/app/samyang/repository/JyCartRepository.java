package com.app.samyang.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.samyang.entity.JyCart;

public interface JyCartRepository extends JpaRepository<JyCart, Long> {
    List<JyCart> findByMemberId(String memberId);
    void deleteByMemberId(String memberId);
}
