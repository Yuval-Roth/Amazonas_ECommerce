package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Notification;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCrudCollection extends CrudCollection<Notification> {
    @Query("SELECT n FROM Notification n WHERE n.receiverId = :receiverId AND n.read = false ORDER BY n.timestamp DESC")
    Iterable<Notification> findUnreadByReceiverId(String receiverId);

    @Query("SELECT n FROM Notification n WHERE n.receiverId = :receiverId ORDER BY n.timestamp DESC")
    Iterable<Notification> findByReceiverId(String receiverId);
}
