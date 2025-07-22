package com.app.samyang.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.app.samyang.entity.JyProduct;
import com.app.samyang.repository.JyProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JyProductService {

    private final JyProductRepository repo;

    public List<JyProduct> findAll() {
        return repo.findAll();
    }

    public JyProduct findById(Long no) {
        return repo.findById(no).orElse(null);
    }

    public void save(JyProduct jyProduct) {
        repo.save(jyProduct);
    }

    public void update(JyProduct jyProduct) {
        repo.save(jyProduct);
    }

    public void delete(Long no) {
        repo.deleteById(no);
    }
    public List<JyProduct> findTop4ByCate(String cate) {
        return repo.findTop4ByCateOrderByNoDesc(cate);
    }
}
