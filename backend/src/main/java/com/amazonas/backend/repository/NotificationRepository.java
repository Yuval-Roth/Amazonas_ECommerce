package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.NotificationCrudCollection;
import com.amazonas.common.dtos.Notification;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component("notificationRepository")
public class NotificationRepository extends AbstractCachingRepository<Notification> {

    private final NotificationCrudCollection repo;

    public NotificationRepository(NotificationCrudCollection repo) {
        super(repo);
        this.repo = repo;
    }

    public List<Notification> findUnreadByReceiverId(String receiverId) {
        List<Notification> unread = new LinkedList<>();
        repo.findUnreadByReceiverId(receiverId).forEach(unread::add);
        return unread;
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit) {
        return findByReceiverId(receiverId, limit, 0);
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit, Integer offset) {
        List<Notification> notifications = new LinkedList<>();
        Iterator<Notification> iter = repo.findByReceiverId(receiverId).iterator();
        for(; offset > 0 && iter.hasNext() ; offset--, iter.next()); {} // skip offset
        for(; limit > 0 && iter.hasNext(); limit--) {
            notifications.add(iter.next());
        }
        return notifications;
    }
}
