package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.DiscountCode;
import com.geraldikem.ecommerceapp.repository.DiscountCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiscountCodeServiceImpl implements DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCodeServiceImpl(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    @Override
    public DiscountCode createDiscountCode(DiscountCode discountCode) {
        // Here you could add validation logic, e.g., to ensure percentage is between 0 and 100
        return discountCodeRepository.save(discountCode);
    }

    @Override
    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    @Override
    @Transactional
    public void toggleDiscountCodeStatus(Long id) {
        DiscountCode discountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount code Id:" + id));
        discountCode.setActive(!discountCode.isActive()); // Flips the boolean value
        discountCodeRepository.save(discountCode);
    }

    @Override
    public void deleteDiscountCode(Long id) {
        discountCodeRepository.deleteById(id);
    }
}