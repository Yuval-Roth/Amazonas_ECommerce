package com.amazonas.backend.repository;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.common.dtos.Product;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class ProductInventoryRepository {


    private final ProductRepository productRepository;

    public ProductInventoryRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<ProductInventory> findById(String storeId) {
        Set<String> products = new HashSet<>(productRepository.findAllProductIdsByStoreId(storeId));
        return Optional.of(new ProductInventory(productRepository, storeId, products));
    }
}
