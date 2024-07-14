package com.amazonas.backend.repository;

import java.util.Objects;

public class CompositeKey2 {
    private final String key1;
    private final String key2;

    private CompositeKey2(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public static CompositeKey2 of(String key1, String key2) {
        return new CompositeKey2(key1, key2);
    }

    public String getKey(){
        return key1+"_"+key2;
    }
}
