package com.amazonas.backend.business.stores.reservations;

import com.amazonas.backend.business.stores.factories.StoreCallbackFactory;
import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.exceptions.ShoppingCartException;
import com.amazonas.backend.repository.ShoppingCartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component("reservationFactory")
public class ReservationFactory {


    private static final Logger log = LoggerFactory.getLogger(ReservationFactory.class);

    private final StoreCallbackFactory storeCallbackFactory;
    private final ShoppingCartRepository shoppingCartRepository;

    public ReservationFactory(StoreCallbackFactory storeCallbackFactory, ShoppingCartRepository shoppingCartRepository) {
        this.storeCallbackFactory = storeCallbackFactory;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public Reservation get(String userId,
                           String storeId,
                           Map<String, Integer> productToQuantity,
                           LocalDateTime expirationDate){

        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(userId);
        if(shoppingCart.isEmpty()) {
            log.error("this should not happen: User does not have a shopping cart");
            throw new IllegalStateException("this should not happen: User does not have a shopping cart");
        }
        Runnable unReserveBasket = () -> {
            try {
                shoppingCart.get().unReserve(storeId);
            } catch (ShoppingCartException e) {
                log.error("this should not happen: Failed to unreserve basket", e);
                throw new IllegalStateException("this should not happen: Failed to unreserve basket");
            }
        };

        return new Reservation(
                userId,
                UUID.randomUUID().toString(),
                storeId,
                productToQuantity,
                expirationDate,
                storeCallbackFactory.cancelReservation(storeId),
                unReserveBasket);
    }
}
