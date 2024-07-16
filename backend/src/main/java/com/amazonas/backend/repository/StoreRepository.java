package com.amazonas.backend.repository;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.common.utils.Rating;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("storeRepository")
public class StoreRepository {
    private final StoreDTORepository storeDTORepository;
    private final StoreFactory storeFactory;
    private final AppointmentSystemRepository apptSysRepo;
    private final ProductInventoryRepository inventoryRepository;

    public StoreRepository(StoreDTORepository storeDTORepository, StoreFactory storeFactory, AppointmentSystemRepository appointmentSystemRepository, ProductInventoryRepository productInventoryRepository) {
        this.storeDTORepository = storeDTORepository;
        this.storeFactory = storeFactory;
        this.apptSysRepo = appointmentSystemRepository;
        this.inventoryRepository = productInventoryRepository;
    }

    public Optional<Store> findById(String storeId) {
        Optional<StoreDTO> dtoOpt = storeDTORepository.findById(storeId);
        return dtoOpt.map(this::storeFromDTO);
    }

    public List<Store> findAllWithRatingAtLeast(Rating rating) {
        return null;
    }

    public List<Store> findAll() {
        return null;
    }

    public boolean storeNameExists(String name) {
        return false;
    }

    public void save(Store toAdd) {

    }

    private Store storeFromDTO(StoreDTO dto) {
        Store store = new Store(dto);
        AppointmentSystem apptSys = apptSysRepo.findById(dto.getId()).orElseThrow(()-> new IllegalStateException("Appointment system not found for store with id: " + dto.getId()));
        ProductInventory inventory = inventoryRepository.findById(dto.getId()).orElseThrow(()-> new IllegalStateException("Product inventory not found for store with id: " + dto.getId()));


        storeFactory.populateDependencies(store);
        store.setAppointmentSystem(apptSys);
        store.setInventory(inventory);
        //TODO: continue adding dependencies
        return store;
    }
}
