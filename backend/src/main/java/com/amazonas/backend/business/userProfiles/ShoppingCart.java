package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.exceptions.PurchaseFailedException;
import com.amazonas.backend.exceptions.ShoppingCartException;
import com.amazonas.backend.repository.CompositeKey2;
import com.amazonas.backend.repository.StoreBasketRepository;
import com.amazonas.common.dtos.ShoppingCartDTO;
import com.amazonas.common.utils.ReadWriteLock;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ShoppingCart {
    private static final Logger log = LoggerFactory.getLogger(ShoppingCart.class);

    private final StoreBasketRepository basketRepository;
    private final StoreBasketFactory storeBasketFactory;
    private final ReadWriteLock lock;

    private final String userId;

    private final Set<String> baskets; // storeIds

    public ShoppingCart(String userId, StoreBasketFactory storeBasketFactory, StoreBasketRepository basketRepository){
        this.storeBasketFactory = storeBasketFactory;
        this.userId = userId;
        this.basketRepository = basketRepository;
        baskets = new HashSet<>();
        lock = new ReadWriteLock();
    }

    //====================================================================================== |
    // =============================== CART MANAGEMENT ===================================== |
    //====================================================================================== |

    @Transactional
    public ShoppingCart mergeGuestCartWithRegisteredCart(ShoppingCart cartOfGuest) {
        try{
            lock.acquireWrite();
            Iterable<StoreBasket> guestBaskets = basketRepository.findAllById(cartOfGuest.getBasketIds());
            for (StoreBasket basket : guestBaskets) {
                StoreBasket userBasket = getBasketOrNew(basket.storeId(), userId).mergeStoreBaskets(basket);
                basketRepository.save(userBasket);
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
            for (var basket : getBaskets()) {
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
            
            for(StoreBasket basket : getBaskets()){
                Reservation r = basket.reserveBasket();

                // If the reservation is null it means that the reservation failed,
                // so we need to cancel all the reservations that were made so far
                if (r == null){
                    reservations.values().forEach(Reservation::cancelReservation);
                    log.debug("Could not reserve some of the products in the cart for user {}.", userId);
                    throw new PurchaseFailedException("Could not reserve some of the products in the cart.");
                }

                // reservation was successful
                reservations.put(basket.storeId(),r);
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
            StoreBasket actualBasket = getBasketOrNew(storeId, getKey(storeId));
            actualBasket.addProduct(productId, quantity);
            basketRepository.save(actualBasket);
            baskets.add(storeId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeProduct(String storeName, String productId) throws ShoppingCartException {

        try{
            lock.acquireWrite();
            StoreBasket basket = getBasketWithValidation(storeName);
            basket.removeProduct(productId);
            if(basket.isEmpty()){
                basketRepository.delete(basket);
                baskets.remove(storeName);
            }
        } finally {
            lock.releaseWrite();
        }
    }

    public void changeProductQuantity(String storeName, String productId,int quantity) throws ShoppingCartException {
        try{
            lock.acquireWrite();
            StoreBasket basket = getBasketWithValidation(storeName);
            basket.changeProductQuantity(productId,quantity);
            basketRepository.save(basket);
        } finally {
            lock.releaseWrite();
        }
    }


    //====================================================================================== |
    // =============================== HELPER METHODS ====================================== |
    //====================================================================================== |
    private StoreBasket getBasketWithValidation(String storeName) throws ShoppingCartException {
        Optional<StoreBasket> basket = basketRepository.findById(getKey(storeName));
        if(basket.isEmpty()){
            throw new ShoppingCartException("Store basket with name: " + storeName + " not found");
        }
        return basket.get();
    }

    private StoreBasket getBasketOrNew(String storeId, String key) {
        return basketRepository.findById(key).orElseGet(() -> storeBasketFactory.get(userId, storeId));
    }

    private Iterable<StoreBasket> getBaskets() {
        return basketRepository.findAllById(getBasketIds());
    }

    private List<String> getBasketIds() {
        return baskets.stream()
                .map(storeId -> getKey(storeId))
                .toList();
    }

    private boolean isCartReservable(){
        for (StoreBasket basket : getBaskets()) {
            if (basket.isReserved()) {
                return false;
            }
        }
        return true;
    }

    private String getKey(String storeId){
        return CompositeKey2.of(userId, storeId).getKey();
    }

    //====================================================================================== |
    // =============================== GETTERS ============================================= |
    //====================================================================================== |
    public String userId() {
        return userId;
    }

    public ShoppingCartDTO getSerializableInstance(){
        ShoppingCartDTO serializable = new ShoppingCartDTO(userId,new HashMap<>());
        for (StoreBasket basket : getBaskets()) {
            serializable.baskets().put(basket.storeId(), basket.getSerializableInstance());
        }
        return serializable;
    }

    public Set<String> baskets() {
        return baskets;
    }
}
