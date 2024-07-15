package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.DiscountManagerCrudCollection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component("discountRepository")
public class DiscountRepository extends AbstractCachingRepository {
    public DiscountRepository(DiscountManagerCrudCollection repo) {
        super(repo);
    }
}
