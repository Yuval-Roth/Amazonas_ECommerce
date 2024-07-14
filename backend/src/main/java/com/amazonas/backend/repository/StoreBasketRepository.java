package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.StoreBasket;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.StoreBasketCrudCollection;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("storeBasketRepository")
public class StoreBasketRepository extends AbstractCachingRepository<StoreBasket> {

    private final StoreBasketCrudCollection repo;

    public StoreBasketRepository(StoreBasketCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public List<String> findBasketIdsByUserId(String userId) {
        List<String> baskets = new LinkedList<>();
        repo.findBasketIdsByUserId(userId).forEach(baskets::add);
        return baskets;
    }

    public void deleteAllByUserId(String userId) {
        repo.deleteAllByUserId(userId);
    }
}
