package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Product;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCrudCollection extends CrudCollection<Product> {
}
