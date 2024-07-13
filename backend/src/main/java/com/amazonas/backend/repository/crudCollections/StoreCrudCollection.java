package com.amazonas.backend.repository.crudCollections;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.common.utils.Rating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCrudCollection extends CrudRepository<Store, String>{

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Store s WHERE s.storeName = ?1")
    boolean existsByName(String name);

    @Query("SELECT s FROM Store s WHERE s.storeRating >= ?1")
    Iterable<Store> findAllWithRatingAtLeast(Rating rating);

}
