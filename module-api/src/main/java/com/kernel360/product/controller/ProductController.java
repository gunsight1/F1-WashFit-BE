package com.kernel360.product.controller;

import com.kernel360.global.jwt.JwtTokenProvider;
import com.kernel360.product.code.ProductsBusinessCode;
import com.kernel360.product.dto.ProductDetailDto;
import com.kernel360.product.dto.ProductDto;
import com.kernel360.product.dto.ProductResponse;
import com.kernel360.product.dto.ProductSearchDto;
import com.kernel360.product.enumset.Sort;
import com.kernel360.product.service.ProductService;
import com.kernel360.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/products")
    ResponseEntity<ApiResponse<List<ProductDto>>> findProductList() {
        final List<ProductDto> products = productService.getProducts();

        return ApiResponse.toResponseEntity(ProductsBusinessCode.GET_PRODUCT_DATA_SUCCESS, products);
    }

    @GetMapping("/products/search")
    ResponseEntity<ApiResponse<Page<ProductResponse>>> findProductByKeyword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(name = "sortType", defaultValue = "viewCnt-order") Sort sortType,
            @RequestParam(value = "keyword", required = false) String keyword, Pageable pageable) {
        
        Page<ProductResponse> productDto;
        String memberId = null;

        if (StringUtils.hasText(token)) {
            memberId = jwtTokenProvider.getSubject(token);
        }

        if (memberId == null) {
            productDto = productService.getProductBySortType(sortType, keyword, pageable);
        } else {
            productDto = productService.searchWithCondition(ProductSearchDto.of(memberId, keyword, sortType), pageable);
        }

        return ApiResponse.toResponseEntity(ProductsBusinessCode.GET_PRODUCT_DATA_SUCCESS, productDto);
    }

    @GetMapping("/product/{productNo}")
    ResponseEntity<ApiResponse<ProductDetailDto>> findProductById(@PathVariable("productNo") Long productNo) {
        ProductDetailDto findProductDetailDto = productService.getProductById(productNo);
        productService.updateViewCount(findProductDetailDto.productNo());

        return ApiResponse.toResponseEntity(ProductsBusinessCode.GET_PRODUCT_DATA_SUCCESS, findProductDetailDto);
    }

    @GetMapping("/products/{OCR_No}")
    ResponseEntity<ApiResponse<Page<ProductDetailDto>>> findProductByOCR(
            @PathVariable("OCR_No") String reportNo, Pageable pageable) {

        return ApiResponse.toResponseEntity(ProductsBusinessCode.GET_PRODUCT_DATA_SUCCESS,
                productService.getProductByOCR(reportNo, pageable));
    }

}
