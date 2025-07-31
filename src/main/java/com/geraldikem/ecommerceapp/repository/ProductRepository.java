package com.geraldikem.ecommerceapp.repository;

import com.geraldikem.ecommerceapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Import this
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByIsFeaturedTrue();
    List<Product> findTop4ByOrderByCreatedAtDesc();
    List<Product> findByStockQuantityLessThanEqual(int stockLevel);
}