package com.amazonas.backend.business.stores;

import com.amazonas.common.utils.Rating;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StoreDTO {
    @Id
    private String storeId;
    private String storeName;
    private boolean isOpen;
    private Rating storeRating;
    private String storeDescription;

    public StoreDTO(String storeId, String storeName, boolean isOpen, Rating storeRating, String storeDescription) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.isOpen = isOpen;
        this.storeRating = storeRating;
        this.storeDescription = storeDescription;
    }

    public StoreDTO () {
        storeId = "";
        storeName = "";
        isOpen = false;
        storeRating = Rating.NOT_RATED;
        storeDescription = "";
    }
}
