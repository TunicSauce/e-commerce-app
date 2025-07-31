package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.dto.CartItemDto;
import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import com.geraldikem.ecommerceapp.model.*;
import com.geraldikem.ecommerceapp.repository.DiscountCodeRepository;
import com.geraldikem.ecommerceapp.repository.ProductRepository;
import com.geraldikem.ecommerceapp.repository.ShoppingCartRepository;
import com.geraldikem.ecommerceapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private static final String SESSION_CART_NAME = "sessionCart";
    private static final String APPLIED_DISCOUNT_CODE = "appliedDiscountCode";

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final TaxService taxService;
    private final DiscountCodeRepository discountCodeRepository;

    public ShoppingCartServiceImpl(UserRepository userRepository,
                                   ProductRepository productRepository,
                                   ShoppingCartRepository shoppingCartRepository,
                                   TaxService taxService,
                                   DiscountCodeRepository discountCodeRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.taxService = taxService;
        this.discountCodeRepository = discountCodeRepository;
    }

    @Override
    @Transactional
    public void addProductToCart(String userEmail, Long productId, int quantity, HttpSession session) {
        if (userEmail != null) {
            addForAuthenticatedUser(userEmail, productId, quantity);
        } else {
            addForGuest(productId, quantity, session);
        }
    }

    @Override
    public ShoppingCartDto getCartForUser(String userEmail, HttpSession session) {
        ShoppingCartDto cartDto = (userEmail != null) ? getCartDtoForUser(userEmail) : getCartDtoForGuest(session);

        String appliedCode = (String) session.getAttribute(APPLIED_DISCOUNT_CODE);
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (appliedCode != null) {
            Optional<DiscountCode> discountOpt = discountCodeRepository.findByCode(appliedCode);
            if (discountOpt.isPresent() && discountOpt.get().isActive()) {
                DiscountCode discount = discountOpt.get();
                BigDecimal percentage = discount.getDiscountPercentage().divide(new BigDecimal("100"));
                discountAmount = cartDto.getSubtotal().multiply(percentage).setScale(2, RoundingMode.HALF_UP);
                cartDto.setAppliedDiscountCode(appliedCode);
            } else {
                session.removeAttribute(APPLIED_DISCOUNT_CODE);
            }
        }

        BigDecimal discountedSubtotal = cartDto.getSubtotal().subtract(discountAmount);
        BigDecimal tax = taxService.calculateTax(discountedSubtotal);
        BigDecimal grandTotal = discountedSubtotal.add(tax);

        cartDto.setDiscountAmount(discountAmount);
        cartDto.setTax(tax);
        cartDto.setGrandTotal(grandTotal);

        return cartDto;
    }

    @Override
    @Transactional
    public void removeProductFromCart(String userEmail, Long productId, HttpSession session) {
        if (userEmail != null) {
            removeForAuthenticatedUser(userEmail, productId);
        } else {
            removeForGuest(productId, session);
        }
    }

    @Override
    @Transactional
    public void updateProductQuantity(String userEmail, Long productId, int quantity, HttpSession session) {
        if (quantity < 0) return;
        if (quantity == 0) {
            removeProductFromCart(userEmail, productId, session);
            return;
        }

        if (userEmail != null) {
            updateForAuthenticatedUser(userEmail, productId, quantity);
        } else {
            updateForGuest(productId, quantity, session);
        }
    }

    @Override
    @Transactional
    public void mergeSessionCartWithUserCart(HttpSession session, String userEmail) {
        SessionCart sessionCart = getSessionCart(session);
        if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
            for (CartItemDto itemDto : sessionCart.getItems()) {
                addForAuthenticatedUser(userEmail, itemDto.getProductId(), itemDto.getQuantity());
            }
            session.removeAttribute(SESSION_CART_NAME);
        }
    }

    @Override
    public boolean applyDiscountCode(HttpSession session, String code) {
        Optional<DiscountCode> discountOpt = discountCodeRepository.findByCode(code);
        if (discountOpt.isPresent() && discountOpt.get().isActive()) {
            session.setAttribute(APPLIED_DISCOUNT_CODE, code);
            return true;
        }
        return false;
    }

    @Override
    public void removeDiscountCode(HttpSession session) {
        session.removeAttribute(APPLIED_DISCOUNT_CODE);
    }


    private ShoppingCartDto getCartDtoForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ShoppingCart cart = shoppingCartRepository.findByUser(user).orElse(new ShoppingCart());
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setItems(cart.getItems().stream().map(item -> {
            CartItemDto itemDto = new CartItemDto();
            Product product = item.getProduct();
            itemDto.setProductId(product.getId());
            itemDto.setProductName(product.getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(product.getPrice());
            return itemDto;
        }).collect(Collectors.toList()));

        BigDecimal subtotal = cartDto.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cartDto.setSubtotal(subtotal);
        return cartDto;
    }

    private ShoppingCartDto getCartDtoForGuest(HttpSession session) {
        SessionCart sessionCart = getSessionCart(session);
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setItems(sessionCart.getItems());
        cartDto.setSubtotal(sessionCart.getTotalPrice());
        return cartDto;
    }

    private void addForAuthenticatedUser(String userEmail, Long productId, int quantity) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ShoppingCart cart = shoppingCartRepository.findByUser(user).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUser(user);
            return newCart;
        });
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProduct(product);
                            newItem.setQuantity(quantity);
                            cart.getItems().add(newItem);
                        }
                );
        shoppingCartRepository.save(cart);
    }

    private void addForGuest(Long productId, int quantity, HttpSession session) {
        SessionCart sessionCart = getSessionCart(session);
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItemDto newItemDto = new CartItemDto();
        newItemDto.setProductId(productId);
        newItemDto.setProductName(product.getName());
        newItemDto.setPrice(product.getPrice());
        newItemDto.setQuantity(quantity);

        sessionCart.addItem(newItemDto);
        session.setAttribute(SESSION_CART_NAME, sessionCart);
    }

    private void removeForAuthenticatedUser(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ShoppingCart cart = shoppingCartRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Cart not found"));
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        shoppingCartRepository.save(cart);
    }

    private void removeForGuest(Long productId, HttpSession session) {
        SessionCart sessionCart = getSessionCart(session);
        sessionCart.removeItem(productId);
        session.setAttribute(SESSION_CART_NAME, sessionCart);
    }

    private void updateForAuthenticatedUser(String userEmail, Long productId, int quantity) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ShoppingCart cart = shoppingCartRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Cart not found"));
        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        shoppingCartRepository.save(cart);
    }

    private void updateForGuest(Long productId, int quantity, HttpSession session) {
        SessionCart sessionCart = getSessionCart(session);
        sessionCart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    sessionCart.recalculateTotalPrice();
                });
        session.setAttribute(SESSION_CART_NAME, sessionCart);
    }

    private SessionCart getSessionCart(HttpSession session) {
        SessionCart sessionCart = (SessionCart) session.getAttribute(SESSION_CART_NAME);
        if (sessionCart == null) {
            sessionCart = new SessionCart();
            session.setAttribute(SESSION_CART_NAME, sessionCart);
        }
        return sessionCart;
    }
}