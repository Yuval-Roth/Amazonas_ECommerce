package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Product;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCrudCollection extends CrudCollection<Product> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.productName = ?1 AND p.storeId = ?2")
    boolean existsByNameAndStoreId(String productName, String storeId);

    @Query("SELECT p FROM Product p WHERE p.storeId = ?1")
    Iterable<Product> findAllByStoreId(String storeId);
}
