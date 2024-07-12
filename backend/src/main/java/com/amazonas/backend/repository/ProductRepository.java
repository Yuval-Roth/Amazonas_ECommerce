package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.ProductCrudCollection;
import com.amazonas.common.dtos.Product;
import org.springframework.stereotype.Component;

@Component("productRepository")
public class ProductRepository extends AbstractCachingRepository<Product> {

    private final ProductCrudCollection repo;

    public ProductRepository(ProductCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public boolean existsByNameAndStoreId(String productName, String storeId) {
        return repo.existsByNameAndStoreId(productName, storeId);
    }

    public Iterable<Product> findAllByStoreId(String storeId) {
        return repo.findAllByStoreId(storeId);
    }
}