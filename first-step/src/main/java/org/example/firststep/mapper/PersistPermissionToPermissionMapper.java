package org.example.firststep.mapper;

import lombok.RequiredArgsConstructor;
import org.example.firststep.exception.ConversationException;
import org.example.firststep.model.external.PersistPermission;
import org.example.firststep.model.external.PersistRole;
import org.example.firststep.model.external.PersistUser;
import org.example.firststep.model.mongo.entity.NameDescription;
import org.example.firststep.model.mongo.entity.SearchableNameDescription;
import org.example.firststep.model.mongo.entity.address.Address;
import org.example.firststep.model.mongo.entity.user.MailAddress;
import org.example.firststep.model.mongo.entity.user.Permission;
import org.example.firststep.model.mongo.entity.user.Role;
import org.example.firststep.model.mongo.entity.user.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PersistPermissionToPermissionMapper {

    private final MongoTemplate mongoTemplate;

    public List<Permission> mapPersistPermissionToPermissionList(List<PersistPermission> persistPermissions) throws ConversationException {
        // Start with inner objects
        List<Permission> permissions = mongoTemplate.find(
                Query.query(Criteria.where("name")
                        .in(persistPermissions.stream().map(PersistPermission::getName).collect(Collectors.toSet()))),
                        Permission.class
        );

        for (PersistPermission persistPermission : persistPermissions) {
            Permission permission = permissions.stream()
                    .filter(p -> p.getNameDescription().getName().equals(persistPermission.getName()))
                    .findFirst()
                    .orElse(null);
            if (permission == null) {
                permission = new Permission();
                setPermissionValues(persistPermission, permissions, permission);
            } else {
                permission.getNameDescription().setDescription(persistPermission.getDescription());
            }
        }

        return permissions;
    }

    private void setPermissionValues(PersistPermission persistPermission, List<Permission> permissions, Permission permission) {
        NameDescription nameDescription = new NameDescription();
        nameDescription.setName(persistPermission.getName());
        nameDescription.setDescription(persistPermission.getDescription());
        permissions.add(permission);
    }
}
