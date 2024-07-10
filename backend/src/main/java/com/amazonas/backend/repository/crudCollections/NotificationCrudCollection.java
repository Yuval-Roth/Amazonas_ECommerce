package com.amazonas.backend.repository.crudCollections;

import com.amazonas.common.dtos.Notification;
import com.amazonas.backend.repository.abstracts.CrudCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCrudCollection extends CrudCollection<Notification> {
}
