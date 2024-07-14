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
        return String.valueOf(this.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeKey2 that = (CompositeKey2) o;
        return Objects.equals(key1, that.key1) && Objects.equals(key2, that.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }
}
