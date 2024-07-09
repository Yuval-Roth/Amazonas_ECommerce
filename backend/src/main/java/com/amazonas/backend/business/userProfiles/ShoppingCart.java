package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.exceptions.PurchaseFailedException;
import com.amazonas.backend.exceptions.ShoppingCartException;
import com.amazonas.common.abstracts.HasId;
import com.amazonas.common.utils.ReadWriteLock;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Entity
public class ShoppingCart implements HasId<String> {
    private static final Logger log = LoggerFactory.getLogger(ShoppingCart.class);

    @Transient
    private StoreBasketFactory storeBasketFactory;
    @Id
    private final String userId;
    @Transient
    private final ReadWriteLock lock;
    @OneToMany
    private final Map<String,StoreBasket> baskets; // storeName --> StoreBasket
    public ShoppingCart(StoreBasketFactory storeBasketFactory, String userId){
        this.storeBasketFactory = storeBasketFactory;
        this.userId = userId;
        baskets = new HashMap<>();
        lock = new ReadWriteLock();
    }

    public ShoppingCart() {
        storeBasketFactory = null;
        userId = null;
        baskets = null;
        lock = new ReadWriteLock();
    }

    //====================================================================================== |
    // =============================== CART MANAGEMENT ===================================== |
    //====================================================================================== |

    public ShoppingCart mergeGuestCartWithRegisteredCart(ShoppingCart cartOfGuest) {
        try{
            lock.acquireWrite();
            for (var entry : cartOfGuest.baskets.entrySet()) {
                String storeId = entry.getKey();
                StoreBasket guestBasket = entry.getValue();
                StoreBasket userBasket = baskets.get(storeId);
                if (userBasket == null) {
                    // If the store ID doesn't exist in the user's cart, add the guest's basket
                    baskets.put(storeId, guestBasket);
                } else {
                    // If the store ID exists in both carts, merge the products
                    userBasket.mergeStoreBaskets(guestBasket);
                }
            }
            return this;
        } finally {
            lock.releaseWrite();
        }
    }

    public double getTotalPrice() {
        try{
            lock.acquireRead();
            double totalPrice = 0;
            for (var basket : baskets.values()) {
                totalPrice += basket.getTotalPrice();
            }
            return totalPrice;
        } finally {
            lock.releaseRead();
        }
    }

    public Map<String, Reservation> reserveCart() throws PurchaseFailedException {
        try{
            lock.acquireWrite();
            if(!isCartReservable()){
                log.debug("Cart is already reserved for user {}.", userId);
                throw new PurchaseFailedException("Cart is already reserved or failed to reserve.");
            }

            if(baskets.isEmpty()){
                log.debug("Cart is empty");
                throw new PurchaseFailedException("Cart is empty");
            }
            Map<String, Reservation> reservations = new HashMap<>();
            for(var entry : baskets.entrySet()){
                Reservation r = entry.getValue().reserveBasket();

                // If the reservation is null it means that the reservation failed,
                // so we need to cancel all the reservations that were made so far
                if (r == null){
                    reservations.values().forEach(Reservation::cancelReservation);
                    log.debug("Could not reserve some of the products in the cart for user {}.", userId);
                    throw new PurchaseFailedException("Could not reserve some of the products in the cart.");
                }

                // reservation was successful
                reservations.put(entry.getKey(),r);
            }
            return reservations;
        } finally {
            lock.releaseWrite();
        }
    }

    public void unReserve(String storeId) throws ShoppingCartException {
        try{
            lock.acquireWrite();
            StoreBasket basket = getBasketWithValidation(storeId);
            basket.unReserve();
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================================== |
    // =============================== BASKET MANAGEMENT =================================== |
    //====================================================================================== |

    public void addProduct(String storeId, String productId, int quantity) throws ShoppingCartException {

        try{
            lock.acquireWrite();
            StoreBasket basket = baskets.computeIfAbsent(storeId, _ -> storeBasketFactory.get(storeId,userId));
            basket.addProduct(productId,quantity);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeProduct(String storeName, String productId) throws ShoppingCartException {

        try{
            lock.acquireWrite();
            StoreBasket basket = getBasketWithValidation(storeName);
            basket.removeProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void changeProductQuantity(String storeName, String productId,int quantity) throws ShoppingCartException {
        try{
            lock.acquireWrite();
            StoreBasket basket = getBasketWithValidation(storeName);
            basket.changeProductQuantity(productId,quantity);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================================== |
    // =============================== HELPER METHODS ====================================== |
    //====================================================================================== |

    private StoreBasket getBasketWithValidation(String storeName) throws ShoppingCartException {
        if(!baskets.containsKey(storeName)){
            throw new ShoppingCartException("Store basket with name: " + storeName + " not found");
        }
        return baskets.get(storeName);
    }

    private boolean isCartReservable(){
        for (StoreBasket basket : baskets.values()) {
            if (basket.isReserved()) {
                return false;
            }
        }
        return true;
    }

    //====================================================================================== |
    // =============================== GETTERS ============================================= |
    //====================================================================================== |
    public String userId() {
        return userId;
    }

    public Map<String, StoreBasket> getBaskets() {
        return baskets;
    }

    @Override
    public String getId() {
        return userId;
    }
}
