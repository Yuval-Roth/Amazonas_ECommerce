package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.ShoppingCartCrudCollection;
import org.springframework.stereotype.Component;

@Component("shoppingCartRepository")
public class ShoppingCartRepository extends AbstractCachingRepository<ShoppingCart> {

    public ShoppingCartRepository(ShoppingCartCrudCollection repo) {
        super(repo);
    }
}