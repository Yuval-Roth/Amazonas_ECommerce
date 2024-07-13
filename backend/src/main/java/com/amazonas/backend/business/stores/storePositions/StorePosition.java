package com.amazonas.backend.business.stores.storePositions;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public record StorePosition(@Id String userId, @Enumerated StoreRole role) { }
