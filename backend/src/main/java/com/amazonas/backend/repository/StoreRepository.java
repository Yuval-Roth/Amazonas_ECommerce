package com.amazonas.backend.repository;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.discountPolicies.DiscountManager;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.business.stores.purchasePolicy.PurchasePolicyManager;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.common.utils.Rating;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component("storeRepository")
public class StoreRepository {
    private final StoreDTORepository storeDTORepository;
    private final StoreFactory storeFactory;
    private final AppointmentSystemRepository apptSysRepo;
    private final ProductInventoryRepository inventoryRepository;
    private final DiscountManagerRepository discountRepository;

    public StoreRepository(StoreDTORepository storeDTORepository, StoreFactory storeFactory, AppointmentSystemRepository appointmentSystemRepository, ProductInventoryRepository productInventoryRepository, DiscountManagerRepository discountRepository) {
        this.storeDTORepository = storeDTORepository;
        this.storeFactory = storeFactory;
        this.apptSysRepo = appointmentSystemRepository;
        this.inventoryRepository = productInventoryRepository;
        this.discountRepository = discountRepository;
    }

    public Optional<Store> findById(String storeId) {
        Optional<StoreDTO> dtoOpt = storeDTORepository.findById(storeId);
        return dtoOpt.map(this::storeFromDTO);
    }

    public List<Store> findAllWithRatingAtLeast(Rating rating) {
        List<Store> stores = new LinkedList<>();
        storeDTORepository.findAllWithRatingAtLeast(rating).forEach(dto -> stores.add(storeFromDTO(dto)));
        return stores;
    }

    public List<Store> findAll() {
        List<Store> stores = new LinkedList<>();
        storeDTORepository.findAll().forEach(dto -> stores.add(storeFromDTO(dto)));
        return stores;
    }

    public boolean storeNameExists(String name) {
        return storeDTORepository.storeNameExists(name);
    }

    public void save(Store toAdd) {
        storeDTORepository.save(new StoreDTO(toAdd.getStoreId(), toAdd.getStoreName(), toAdd.getStoreDescription(), toAdd.getStoreRating(), toAdd.isOpen()));
        apptSysRepo.save(toAdd.getAppointmentSystem());
        discountRepository.save(toAdd.getDiscountManager());
        //TODO: save purchase policy manager

        // the product inventory is saved on the fly with every operation
        // so we don't need to save it here
    }

    private Store storeFromDTO(StoreDTO dto) {
        Store store = new Store(dto);
        AppointmentSystem apptSys = apptSysRepo.findById(dto.getId()).orElseThrow(()-> new IllegalStateException("Appointment system not found for store with id: " + dto.getId()));
        ProductInventory inventory = inventoryRepository.findById(dto.getId()).orElseThrow(()-> new IllegalStateException("Product inventory not found for store with id: " + dto.getId()));
        DiscountManager discountManager = discountRepository.findById(dto.getId()).orElseThrow(()-> new IllegalStateException("Discount manager not found for store with id: " + dto.getId()));

        storeFactory.populateDependencies(store);
        store.setAppointmentSystem(apptSys);
        store.setInventory(inventory);
        store.setDiscountManager(discountManager);
        store.setPurchasePolicyManager(new PurchasePolicyManager()); // not persisted yet
        //TODO: continue adding dependencies
        return store;
    }
}
