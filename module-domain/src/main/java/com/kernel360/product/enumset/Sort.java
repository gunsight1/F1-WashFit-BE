package com.kernel360.product.enumset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sort {
    VIEW_COUNT_PRODUCT_ORDER("viewCnt-order"),
    VIOLATION_PRODUCT_LIST("violation-products"),
    RECOMMENDATION_PRODUCT_ORDER("recommend-order"),
    RECENT_PRODUCT_ORDER("recent-order");

    private final String orderType;
}
