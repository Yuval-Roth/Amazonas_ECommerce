package com.amazonas.backend.business.stores.factories;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.repository.DiscountRepository;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.utils.Rating;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("storeFactory")
public class StoreFactory {

    private final ReservationFactory reservationFactory;
    private final PendingReservationMonitor pendingReservationMonitor;
    private final PermissionsController permissionsController;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public StoreFactory(ReservationFactory reservationFactory,
                        PendingReservationMonitor pendingReservationMonitor,
                        PermissionsController permissionsController,
                        TransactionRepository transactionRepository,
                        ProductRepository productRepository,
                        DiscountRepository discountRepository) {
        this.reservationFactory = reservationFactory;
        this.pendingReservationMonitor = pendingReservationMonitor;
        this.permissionsController = permissionsController;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public Store get(String founderUserId, String storeName, String description){
        String storeId = UUID.randomUUID().toString();
        return new Store(storeId,
                storeName,
                description,
                Rating.NOT_RATED,
                new ProductInventory(productRepository, storeId),
                new AppointmentSystem(founderUserId,storeId),
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository,
                discountRepository);
    }

    public void populateDependencies(Store store){
        store.setPendingReservationMonitor(pendingReservationMonitor);
        store.setPermissionsController(permissionsController);
        store.setReservationFactory(reservationFactory);
        store.setTransactionRepository(transactionRepository);
    }
}
