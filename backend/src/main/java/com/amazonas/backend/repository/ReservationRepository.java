package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("reservationRepository")
public class ReservationRepository {

    private final Map<String, List<Reservation>> reservationCache;
    private final ReadWriteLock reservationLock;

    public ReservationRepository() {
        reservationCache = new HashMap<>();
        reservationLock = new ReadWriteLock();
    }

    public void save(Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.computeIfAbsent(reservation.getId(), _ -> new LinkedList<>()).add(reservation);
        } finally {
            reservationLock.releaseWrite();
        }
    }

    public List<Reservation> findAllById(String userId){
        reservationLock.acquireRead();
        try {
            return reservationCache.getOrDefault(userId, Collections.emptyList());
        } finally {
            reservationLock.releaseRead();
        }
    }

    public void deleteReservation(String userId, Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.getOrDefault(userId, Collections.emptyList()).remove(reservation);
            if(reservationCache.get(userId).isEmpty()){
                reservationCache.remove(userId);
            }
        } finally {
            reservationLock.releaseWrite();
        }
    }

}