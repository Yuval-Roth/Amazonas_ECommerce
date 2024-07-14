package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.repository.StoreBasketRepository;
import org.springframework.stereotype.Component;

@Component("shoppingCartFactory")
public class ShoppingCartFactory {

    private final StoreBasketFactory storeBasketFactory;
    private final StoreBasketRepository storeBasketRepository;

    public ShoppingCartFactory(StoreBasketFactory storeBasketFactory, StoreBasketRepository storeBasketRepository) {
        this.storeBasketFactory = storeBasketFactory;
        this.storeBasketRepository = storeBasketRepository;
    }

    public ShoppingCart get(String userId){
            return new ShoppingCart(userId,
                    storeBasketFactory,
                    storeBasketRepository);
        }
}
