package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.business.userProfiles.StoreBasketFactory;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.ShoppingCartCrudCollection;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component("shoppingCartRepository")
public class ShoppingCartRepository extends AbstractCachingRepository<ShoppingCart> {

    private final StoreBasketFactory storeBasketFactory;

    public ShoppingCartRepository(ShoppingCartCrudCollection repo, StoreBasketFactory storeBasketFactory) {
        super(repo);
        this.storeBasketFactory = storeBasketFactory;
    }

    @Override
    public Iterable<ShoppingCart> findAllById(Iterable<String> strings) {
        List<ShoppingCart> allById = new LinkedList<>();
        super.findAllById(strings).forEach(allById::add);
        allById.forEach(c -> c.setStoreBasketFactory(storeBasketFactory));
        return allById;
    }

    @Override
    public Optional<ShoppingCart> findById(String s) {
        Optional<ShoppingCart> byId = super.findById(s);
        byId.ifPresent(c -> c.setStoreBasketFactory(storeBasketFactory));
        return byId;
    }

    @Override
    public Iterable<ShoppingCart> findAll() {
        List<ShoppingCart> all = new LinkedList<>();
        super.findAll().forEach(all::add);
        all.forEach(c -> c.setStoreBasketFactory(storeBasketFactory));
        return all;
    }
}