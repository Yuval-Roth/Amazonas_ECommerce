package com.amazonas.common.dtos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record StoreBasketDTO (
    Map<String, Integer> products,
    boolean reserved
){
    public Map<String, Integer> getProducts() {
        return products;
    }
}
