package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.business.stores.storePositions.OwnerNode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("appointmentSystemRepository")
public class AppointmentSystemRepository {

    private final OwnerNodeRepository ownerNodeRepository;

    public AppointmentSystemRepository(OwnerNodeRepository ownerNodeRepository) {
        this.ownerNodeRepository = ownerNodeRepository;
    }
    public Optional<AppointmentSystem> findById(String storeId) {
        Optional<OwnerNode> root = ownerNodeRepository.findRootNodeByStoreId(storeId);
        return root.map(ownerNode -> new AppointmentSystem(ownerNode, ownerNodeRepository));
    }

    public void save(AppointmentSystem appt) {
        ownerNodeRepository.save(appt.getRoot());
    }
}
