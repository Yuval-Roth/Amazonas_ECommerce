package com.amazonas.backend.business.stores.storePositions;

import com.amazonas.backend.repository.CompositeKey2;
import com.amazonas.backend.repository.OwnerNodeRepository;
import com.amazonas.common.abstracts.HasId;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Entity
public class OwnerNode implements HasId<String> {

    private static final Logger log = LoggerFactory.getLogger(OwnerNode.class);

    @Id
    private String ownerNodeId;
    private final String storeId;
    private final String userId;
    @ElementCollection
    private List<String> ownersChildren;
    @ElementCollection
    private List<String> managersChildren;
    private final String parent; // parent userId
    @Transient
    private OwnerNodeRepository repo;

    public OwnerNode(String userId, String parent, String storeId, OwnerNodeRepository repo) {
        this.userId = userId;
        this.parent = parent;
        this.repo = repo;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
        this.storeId = storeId;
        ownerNodeId = getKey(userId, storeId);
    }

    public OwnerNode() {
        this.userId = "";
        storeId = "";
        this.parent = null;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
        repo = null;
    }

    public String getUserId() {
        return userId;
    }

    public OwnerNode addOwner(String userID) {
        OwnerNode userNode = new OwnerNode(userID, this.userId, storeId,repo);
        repo.save(userNode);
        ownersChildren.add(userNode.userId);
        return userNode;
    }

    public void addManager(String userID) {
        managersChildren.add(userID);
        repo.save(this);
    }

    /**
     *
     * @param userID
     * @return userID if the action successfully done, otherwise returns null
     */
    public void deleteOwner(String userID) {
        Iterator<String> iter = ownersChildren.iterator();
        while (iter.hasNext()) {
            String owner = iter.next(); // must be called before you can call i.remove()
            if (owner != null && owner.equalsIgnoreCase(userID)) {
                repo.deleteById(getKey(userID, storeId));
                iter.remove();
            }
        }
    }

    public void deleteManager(String userID) {
        // must be called before you can call i.remove()
        managersChildren.removeIf(manager -> manager.equals(userID));
    }

    public List<String> getAllChildren() {
        List<String> ret = new LinkedList<>(managersChildren);
        ret.add(userId);
        for(String child : managersChildren) {
            ret.addAll(getNode(child).getAllChildren());
        }
        return ret;
    }

    private OwnerNode getNode(String userId) {
        Optional<OwnerNode> node = repo.findById(getKey(userId, storeId));
        if (node.isEmpty()) {
            log.error("could not find OwnerNode with id: {}", userId);
            throw new IllegalStateException("An error has occurred, check the logs for more information");
        }
        return node.get();
    }

    public List<String> getAllOwners() {
        List<String> ret = new LinkedList<>();
        if(parent != null) {
            ret.add(getUserId());
        }
        for(String child : ownersChildren) {
            ret.addAll(getNode(child).getAllOwners());
        }
        return ret;
    }

    public List<String> getAllManagers() {
        List<String> ret = new LinkedList<>(managersChildren);
        for(String child : managersChildren) {
            ret.addAll(getNode(child).getAllManagers());
        }
        return ret;
    }

    public OwnerNode search(String userId){
        if (this.userId.equalsIgnoreCase(userId)){
            return this;
        }
        for (String owner : ownersChildren){
            OwnerNode found = getNode(owner).search(userId);
            if (found != null){
                return found;
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return userId;
    }
    private String getKey(String userID, String storeId) {
        return CompositeKey2.of(userID, storeId).getKey();
    }

    public boolean isOwner(String userId) {
        return getAllOwners().contains(userId);
    }

    public boolean isManager(String userId) {
        return getAllManagers().contains(userId);
    }

    public void setRepo(OwnerNodeRepository repo) {
        this.repo = repo;
    }

    public String getStoreId() {
        return storeId;
    }
}