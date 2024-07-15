package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("appointmentSystemRepository")
public class AppointmentSystemRepository {

    private final OwnerNodeRepository ownerNodeRepository;

    public AppointmentSystemRepository(OwnerNodeRepository ownerNodeRepository) {
        this.ownerNodeRepository = ownerNodeRepository;
    }

    public Optional<AppointmentSystem> findById(String storeId) {
        return Optional.empty();
    }

}
