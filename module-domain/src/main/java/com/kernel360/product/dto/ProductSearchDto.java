package com.kernel360.product.dto;

import com.kernel360.product.enumset.Sort;

public record ProductSearchDto(
        String memberId,
        String keyword,
        Sort sortType

) {
    public static ProductSearchDto of(String memberId, String keyword, Sort sortBy) {
        return new ProductSearchDto(memberId, keyword, sortBy);
    }
}
