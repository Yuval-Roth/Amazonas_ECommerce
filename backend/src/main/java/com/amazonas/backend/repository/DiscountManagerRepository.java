package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.discountPolicies.DiscountManager;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.DiscountManagerCrudCollection;
import org.springframework.stereotype.Component;

@Component("discountRepository")
public class DiscountManagerRepository extends AbstractCachingRepository<DiscountManager> {
    public DiscountManagerRepository(DiscountManagerCrudCollection repo) {
        super(repo);
    }
}
