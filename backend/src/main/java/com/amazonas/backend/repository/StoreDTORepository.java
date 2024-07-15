package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.StoreCrudCollection;
import com.amazonas.common.utils.Rating;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("storeDTORepository")
public class StoreDTORepository extends AbstractCachingRepository<StoreDTO> {

    private final StoreCrudCollection repo;

    public StoreDTORepository(StoreCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public boolean storeNameExists(String name){
        return repo.existsByName(name);
    }

    public Iterable<StoreDTO> findAllWithRatingAtLeast(Rating rating) {
        return repo.findAllWithRatingAtLeast(rating);
    }
}