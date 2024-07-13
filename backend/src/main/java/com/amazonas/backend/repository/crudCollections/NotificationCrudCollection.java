package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCrudCollection extends CrudRepository<Notification, String> {
    @Query("SELECT n FROM Notification n WHERE n.receiverId = :receiverId AND n.read = false ORDER BY n.timestamp DESC")
    Iterable<Notification> findUnreadByReceiverId(String receiverId);

    @Query("SELECT n FROM Notification n WHERE n.receiverId = :receiverId ORDER BY n.timestamp DESC")
    Iterable<Notification> findByReceiverId(String receiverId);
}
