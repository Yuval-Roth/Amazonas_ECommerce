package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.crudCollections.PermissionProfileCrudCollection;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component("permissionsProfileRepository")
public class PermissionsProfileRepository extends AbstractCachingRepository<UserPermissionsProfile> {

    private final DefaultPermissionsProfile defaultProfile;

    public PermissionsProfileRepository(PermissionProfileCrudCollection repo, @Qualifier("defaultRegisteredUserPermissionsProfile") DefaultPermissionsProfile defaultRegisteredUserPermissionsProfile) {
        super(repo);
        this.defaultProfile = defaultRegisteredUserPermissionsProfile;
    }

    @Override
    public Iterable<UserPermissionsProfile> findAllById(Iterable<String> strings) {
        List<UserPermissionsProfile> allById = new LinkedList<>();
        super.findAllById(strings).forEach(allById::add);
        allById.forEach(p -> p.setDefaultProfile(defaultProfile));
        return allById;
    }

    @Override
    public Optional<UserPermissionsProfile> findById(String s) {
        Optional<UserPermissionsProfile> byId = super.findById(s);
        byId.ifPresent(p -> p.setDefaultProfile(defaultProfile));
        return byId;
    }

    @Override
    public Iterable<UserPermissionsProfile> findAll() {
        List<UserPermissionsProfile> all = new LinkedList<>();
        super.findAll().forEach(all::add);
        all.forEach(p -> p.setDefaultProfile(defaultProfile));
        return all;
    }
}