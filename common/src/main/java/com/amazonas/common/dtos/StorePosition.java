package com.amazonas.common.dtos;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public record StorePosition(@Id String userId, @Enumerated StoreRole role) { }
