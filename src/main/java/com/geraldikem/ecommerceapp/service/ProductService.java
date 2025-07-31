package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAllProducts();
    Optional<Product> findProductById(Long id);
    Product saveProduct(Product product);
    void deleteProductById(Long id);
    Page<Product> findPaginated(int pageNo, int pageSize);
    List<Product> findFeaturedProducts();
    List<Product> findNewestProducts();
    List<Product> findLowStockProducts(int threshold);
    Page<Product> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize);
    Product saveProduct(Product product, MultipartFile imageFile) throws IOException;
}