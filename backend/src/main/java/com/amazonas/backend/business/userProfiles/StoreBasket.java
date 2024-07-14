package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.exceptions.ShoppingCartException;
import com.amazonas.backend.repository.CompositeKey2;
import com.amazonas.common.abstracts.HasId;
import com.amazonas.common.dtos.StoreBasketDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Entity
public class StoreBasket implements HasId<String> {
    private static final Logger log = LoggerFactory.getLogger(StoreBasket.class);

    @Id
    private final String basketId;
    private final String userId;
    private final String storeId;
    private boolean reserved;
    @ElementCollection
    private Map<String, Integer> products; // productId --> quantity

    @Transient /*@Cascade(CascadeType.ALL)*/
    private final Function<Map<String,Integer>, Reservation> makeReservation;
    @Transient /*@Cascade(CascadeType.ALL)*/
    private final Function<Map<String, Integer>, Double> calculatePrice;

    public StoreBasket (Function<Map<String,Integer>,
                        Reservation> makeReservation,
                        Function<Map<String,Integer>,Double> calculatePrice,
                        String userId, String storeId){

        this.makeReservation = makeReservation;
        this.calculatePrice = calculatePrice;
        products = new HashMap<>();
        this.storeId = storeId;
        this.userId = userId;
        basketId = CompositeKey2.of(userId, storeId).getKey();
    }

    public StoreBasket() {
        basketId = "";
        userId = "";
        storeId = "";
        makeReservation = _ -> {
            throw new UnsupportedOperationException("function not set");
        };
        calculatePrice = _ -> {
            throw new UnsupportedOperationException("function not set");
        };
        products = new HashMap<>();
    }

    public void addProduct(String productId, int quantity) throws ShoppingCartException {

        if(reserved){
            log.debug("Basket is already reserved, cannot add products to it.");
            throw new ShoppingCartException("Basket is already reserved, cannot add products to it.");
        }

        if(quantity <= 0){
            log.debug("Quantity cannot be 0 or less");
            throw new ShoppingCartException("Quantity cannot be 0 or less");
        }
        if(products.containsKey(productId)){
            log.debug("Product is already exists, change the quantity of the product if needed");
            throw new ShoppingCartException("Product is already exists, change the quantity of the product if needed");
        }
        products.put(productId, quantity);
    }
    public void removeProduct(String productId) throws ShoppingCartException {

        if(reserved){
            log.debug("Basket is already reserved, cannot remove products from it");
            throw new ShoppingCartException("Basket is already reserved, cannot remove products from it");
        }

        if(!products.containsKey(productId)){
            log.debug("Product with id : {} not found",productId);
            throw new ShoppingCartException("Product with id: " + productId + " not found");
        }
        products.remove(productId);
    }

    public void changeProductQuantity(String productId, int quantity) throws ShoppingCartException {

        if(reserved){
            log.debug("Basket is already reserved, cannot change the quantity of the products");
            throw new ShoppingCartException("Basket is already reserved, cannot change the quantity of the products");
        }

        if(quantity <= 0){
            log.debug("Quantity cannot be 0 or less");
            throw new ShoppingCartException("Quantity cannot be 0 or less");
        }
        if(!products.containsKey(productId)){
            log.debug("Product with id : {} not found",productId);
          throw new ShoppingCartException("Product with id: " + productId + " not found");
        }
        products.put(productId, quantity);
    }

    public StoreBasket mergeStoreBaskets(StoreBasket guestBasket) {
        for (var entry : guestBasket.products.entrySet()) {
            if (! products.containsKey(entry.getKey())) {
                // If the product ID doesn't exist in the user's basket, add the guest's product
                products.put(entry.getKey(), entry.getValue());
            } else {
                // If the product ID exists in both baskets, update the quantity
                products.put(entry.getKey(), products.get(entry.getKey()) + entry.getValue());
            }
        }
        return this;
    }

    public Reservation reserveBasket() {
        if(reserved){
            log.debug("Basket has already been reserved");
            return null;
        }
        Reservation reservation = makeReservation.apply(getProducts());
        if(reservation != null){
            reserved = true;
        }
        return reservation;
    }

    public double getTotalPrice() {
        return calculatePrice.apply(getProducts());
    }

    public Map<String,Integer> getProducts() {
        return products;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void unReserve() {
        reserved = false;
    }

    public StoreBasketDTO getSerializableInstance() {
        return new StoreBasketDTO(products, reserved);
    }

    @Override
    public String getId() {
        return basketId;
    }

    public String storeId() {
        return storeId;
    }

    public String userId() {
        return userId;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
