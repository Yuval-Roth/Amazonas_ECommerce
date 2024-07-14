package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.business.userProfiles.ShoppingCartFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("shoppingCartRepository")
public class ShoppingCartRepository {

    private final StoreBasketRepository repo;
    private final ShoppingCartFactory factory;
    private final UserRepository userRepository;

    private final Map<String,ShoppingCart> cache;

    public ShoppingCartRepository(StoreBasketRepository storeBasketRepository,
                                  ShoppingCartFactory shoppingCartFactory, UserRepository userRepository) {
        this.repo = storeBasketRepository;
        this.factory = shoppingCartFactory;
        this.userRepository = userRepository;
        cache = new HashMap<>();
    }

    public Optional<ShoppingCart> findById(String userId) {
//        if (cache.containsKey(userId)) {
//            return Optional.of(cache.get(userId));
//        }

        if(! userRepository.existsById(userId)) {
            return Optional.empty();
        }
        ShoppingCart shoppingCart = factory.get(userId);
        shoppingCart.baskets().addAll(repo.findStoreIdsByUserId(userId));
//        cache.put(userId, shoppingCart);
        return Optional.of(shoppingCart);
    }

    public void resetCart(String userId) {
        repo.deleteAllByUserId(userId);
    }
}