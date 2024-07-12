package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.StoreCrudCollection;
import com.amazonas.common.utils.Rating;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("storeRepository")
public class StoreRepository extends AbstractCachingRepository<Store> {

    private final StoreCrudCollection repo;

    public StoreRepository(StoreCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public boolean storeNameExists(String name){
        return repo.existsByName(name);
    }

    public List<Store> findAllWithRatingAtLeast(Rating rating) {
        List<Store> stores = new LinkedList<>();
        repo.findAllWithRatingAtLeast(rating).forEach(stores::add);
        return stores;
    }
}