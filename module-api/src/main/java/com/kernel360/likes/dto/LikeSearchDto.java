package com.kernel360.likes.dto;

import com.kernel360.product.enumset.Sort;

public record LikeSearchDto(
        String memberId,
        String keyword,
        Sort sortType
) {
    public static LikeSearchDto of(String memberId, String keyword, Sort sortBy) {
        return new LikeSearchDto(memberId, keyword, sortBy);
    }
}
