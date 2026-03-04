package com.kernel360.admin.controller;

import com.kernel360.admin.service.AdminProductService;
import com.kernel360.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public String getAllProducts(Pageable pageable, Model model) {
        Page<ProductDto> products = adminProductService.getProducts(pageable);
        model.addAttribute("products", products);
        return "admin/products";
    }
}
