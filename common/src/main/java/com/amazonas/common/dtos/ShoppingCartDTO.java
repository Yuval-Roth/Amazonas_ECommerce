package com.amazonas.common.dtos;

import java.util.Map;

public record ShoppingCartDTO (
        String userId,
        Map<String, StoreBasketDTO> baskets // storeId -> StoreBasket
){
    public Map<String, StoreBasketDTO> getBaskets() {
        return baskets;
    }
}
