package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.Role;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.repository.RoleRepository;
import com.geraldikem.ecommerceapp.service.OrderService;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.ReviewService;
import com.geraldikem.ecommerceapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(ProductService productService, OrderService orderService, 
                          ReviewService reviewService, UserService userService, RoleRepository roleRepository) {
        this.productService = productService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        BigDecimal totalRevenue = orderService.getTotalRevenue();
        long totalOrders = orderService.getTotalOrdersCount();
        List<Order> recentOrders = orderService.getRecentOrders();

        List<Product> lowStockProducts = productService.findLowStockProducts(5);

        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("recentOrders", recentOrders);

        model.addAttribute("lowStockProducts", lowStockProducts);

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    // This is the single correct method for adding a product
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        productService.saveProduct(product, imageFile);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Product product = productService.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "admin/update-product";
    }

    // This is the single correct method for updating a product
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") long id,
                                @ModelAttribute("product") Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        product.setId(id);
        productService.saveProduct(product, imageFile);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/orders/{orderId}")
    public String showOrderDetail(@PathVariable Long orderId, Model model) {
        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderId));
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders/" + orderId;
    }

    // Reviews Management
    @GetMapping("/reviews")
    public String listReviews(Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/admin/reviews";
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "admin/users";
    }

    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userService.updateUserRole(userId, roleId);
        return "redirect:/admin/users";
    }

    // Orders Management
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
}