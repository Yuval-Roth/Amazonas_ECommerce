package com.amazonas.backend.repository;

import com.amazonas.common.abstracts.HasId;
import com.amazonas.common.utils.Rating;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "Store")
public class StoreDTO implements HasId<String> {
    @Id
    private String storeId;
    private String storeName;
    private String storeDescription;
    private Rating storeRating;
    private boolean isOpen;

    public StoreDTO(String storeId, String storeName, String storeDescription, Rating storeRating, boolean isOpen) {
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

    @Override
    public String getId() {
        return storeId;
    }
    public String storeName() {
        return storeName;
    }

    public String storeDescription() {
        return storeDescription;
    }

    public Rating storeRating() {
        return storeRating;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
