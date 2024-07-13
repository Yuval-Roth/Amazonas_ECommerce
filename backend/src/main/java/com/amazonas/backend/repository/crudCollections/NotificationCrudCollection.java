package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCrudCollection extends CrudRepository<Notification, String> {

    @Query("SELECT n FROM Notification n WHERE n.receiverId = ?1 AND n.isRead = false ORDER BY n.timestamp desc")
    Iterable<Notification> findUnreadByReceiverId(String receiverId);

    @Query("SELECT n FROM Notification n WHERE n.receiverId = ?1 ORDER BY n.timestamp desc")
    Iterable<Notification> findByReceiverId(String receiverId);
}
