package com.amazonas.backend.business.stores.factories;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.repository.StoreDTORepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {

    private final StoreDTORepository storeRepository;

    public StoreCallbackFactory(StoreDTORepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Function<Map<String,Integer>, Double> calculatePrice(String storeId){
        return products -> storeRepository.findById(storeId).orElseThrow().calculatePrice(products);
    }

    public Function<Map<String,Integer>, Reservation> makeReservation(String storeId, String userId){
        return products -> storeRepository.findById(storeId).orElseThrow().reserveProducts(products,userId);
    }

    public Function<Reservation,Boolean> cancelReservation(String storeId){
        return reservation -> storeRepository.findById(storeId).orElseThrow().cancelReservation(reservation);
    }

}
