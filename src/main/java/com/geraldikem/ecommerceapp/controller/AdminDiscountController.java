package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.DiscountCode;
import com.geraldikem.ecommerceapp.service.DiscountCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/discounts")
public class AdminDiscountController {

    private final DiscountCodeService discountCodeService;

    public AdminDiscountController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @GetMapping
    public String listDiscountCodes(Model model) {
        model.addAttribute("discounts", discountCodeService.getAllDiscountCodes());
        return "admin/discounts";
    }

    @GetMapping("/add")
    public String showAddDiscountForm(Model model) {
        model.addAttribute("discount", new DiscountCode());
        return "admin/add-discount";
    }

    @PostMapping("/add")
    public String addDiscountCode(@ModelAttribute("discount") DiscountCode discountCode) {
        discountCodeService.createDiscountCode(discountCode);
        return "redirect:/admin/discounts";
    }

    @GetMapping("/toggle/{id}")
    public String toggleDiscountStatus(@PathVariable Long id) {
        discountCodeService.toggleDiscountCodeStatus(id);
        return "redirect:/admin/discounts";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.deleteDiscountCode(id);
        return "redirect:/admin/discounts";
    }
}