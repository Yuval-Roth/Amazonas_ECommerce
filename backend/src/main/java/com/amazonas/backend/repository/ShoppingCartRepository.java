package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.business.userProfiles.ShoppingCartFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
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
        shoppingCart.baskets().addAll(repo.findBasketIdsByUserId(userId));
        return Optional.of(shoppingCart);
    }

    public void resetCart(String userId) {
        repo.deleteAllByUserId(userId);
    }
}