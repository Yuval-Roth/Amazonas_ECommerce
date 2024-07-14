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

    public ShoppingCartRepository(StoreBasketRepository storeBasketRepository,
                                  ShoppingCartFactory shoppingCartFactory, UserRepository userRepository) {
        this.repo = storeBasketRepository;
        this.factory = shoppingCartFactory;
        this.userRepository = userRepository;
    }

    public Optional<ShoppingCart> findById(String userId) {
        if(! userRepository.existsById(userId)) {
            return Optional.empty();
        }
        ShoppingCart shoppingCart = factory.get(userId);
        shoppingCart.baskets().addAll(repo.findStoreIdsByUserId(userId));
        return Optional.of(shoppingCart);
    }

    public void resetCart(String userId) {
        repo.deleteAllByUserId(userId);
    }
}