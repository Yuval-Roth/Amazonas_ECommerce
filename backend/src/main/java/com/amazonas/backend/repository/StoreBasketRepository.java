package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.StoreBasket;
import com.amazonas.backend.business.userProfiles.StoreBasketFactory;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.StoreBasketCrudCollection;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component("storeBasketRepository")
public class StoreBasketRepository extends AbstractCachingRepository<StoreBasket> {

    private final StoreBasketCrudCollection repo;
    private final StoreBasketFactory factory;

    public StoreBasketRepository(StoreBasketCrudCollection repo, StoreBasketFactory storeBasketFactory) {
        super(repo);
        this.repo = repo;
        this.factory = storeBasketFactory;
    }

    public List<String> findBasketIdsByUserId(String userId) {
        List<String> baskets = new LinkedList<>();
        repo.findBasketIdsByUserId(userId).forEach(baskets::add);
        return baskets;
    }

    public void deleteAllByUserId(String userId) {
        repo.deleteAllByUserId(userId);
    }

    @Override
    public Iterable<StoreBasket> findAllById(Iterable<String> strings) {
        List<StoreBasket> baskets = new LinkedList<>();
        super.findAllById(strings).forEach(baskets::add);
        baskets.forEach(factory::populateDependencies);
        return baskets;
    }

    @Override
    public Optional<StoreBasket> findById(String s) {
        Optional<StoreBasket> byId = super.findById(s);
        byId.ifPresent(factory::populateDependencies);
        return byId;
    }

    /**
     * Returns all entities from the repository. If caching is enabled, the entities are stored in the cache.
     *
     * @apiNote this does not read from the cache, it always reads from the repository
     */
    @Override
    public Iterable<StoreBasket> findAll() {
        List<StoreBasket> baskets = new LinkedList<>();
        super.findAll().forEach(baskets::add);
        baskets.forEach(factory::populateDependencies);
        return baskets;
    }

    public List<String> findStoreIdsByUserId(String userId) {
        List<String> storeIds = new LinkedList<>();
        repo.findStoreIdsByUserId(userId).forEach(storeIds::add);
        return storeIds;
    }
}
