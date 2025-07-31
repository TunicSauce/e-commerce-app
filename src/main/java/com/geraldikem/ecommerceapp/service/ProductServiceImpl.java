package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.geraldikem.ecommerceapp.repository.ProductSpecification;
import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Fetches a specific page of products from the database.
     * @param pageNo The page number to retrieve (1-based).
     * @param pageSize The number of products on each page.
     * @return A Page object containing the products and pagination info.
     */
    @Override
    public Page<Product> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue();
    }

    @Override
    public List<Product> findNewestProducts() {
        return productRepository.findTop4ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold);
    }

    @Override
    public Page<Product> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize) {
        Specification<Product> spec = ProductSpecification.findByCriteria(keyword, minPrice, maxPrice);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product saveProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            product.setImageUrl("/product-images/" + fileName);
        }

        return productRepository.save(product);
    }
}