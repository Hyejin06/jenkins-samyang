package com.app.samyang.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.samyang.entity.HjCart;

public interface HjCartRepository extends JpaRepository<HjCart, Long> {
    List<HjCart> findByMemberId(String memberId);
    void deleteByMemberId(String memberId);
}
