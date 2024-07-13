package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.StoreCrudCollection;
import com.amazonas.common.utils.Rating;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component("storeRepository")
public class StoreRepository extends AbstractCachingRepository<Store> {

    private final StoreCrudCollection repo;
    private final StoreFactory storeFactory;

    public StoreRepository(StoreCrudCollection repo, @Lazy StoreFactory storeFactory) {
        super(repo);
        this.repo = repo;
        this.storeFactory = storeFactory;
    }

    public boolean storeNameExists(String name){
        return repo.existsByName(name);
    }

    public List<Store> findAllWithRatingAtLeast(Rating rating) {
        List<Store> stores = new LinkedList<>();
        repo.findAllWithRatingAtLeast(rating).forEach(stores::add);
        stores.forEach(storeFactory::populateDependencies);
        return stores;
    }

    @Override
    public Iterable<Store> findAllById(Iterable<String> strings) {
        Iterable<Store> allById = super.findAllById(strings);
        allById.forEach(storeFactory::populateDependencies);
        return allById;
    }

    @Override
    public Optional<Store> findById(String s) {
        Optional<Store> byId = super.findById(s);
        byId.ifPresent(storeFactory::populateDependencies);
        return byId;
    }

    @Override
    public Iterable<Store> findAll() {
        Iterable<Store> all = super.findAll();
        all.forEach(storeFactory::populateDependencies);
        return  all;
    }
}