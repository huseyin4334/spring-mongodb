package org.example.firststep.mapper;

import lombok.RequiredArgsConstructor;
import org.example.firststep.exception.ConversationException;
import org.example.firststep.model.external.PersistPermission;
import org.example.firststep.model.external.PersistRole;
import org.example.firststep.model.mongo.entity.NameDescription;
import org.example.firststep.model.mongo.entity.user.Permission;
import org.example.firststep.model.mongo.entity.user.Role;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PersistRoleToRoleMapper {

    private final MongoTemplate mongoTemplate;
    private final PersistPermissionToPermissionMapper persistPermissionToPermissionMapper;

    public List<Role> mapPersistRoleToRoleList(List<PersistRole> persistRoles) throws ConversationException {
        // Start with inner objects
        List<Role> roles = mongoTemplate.find(
                Query.query(Criteria.where("name")
                        .in(persistRoles.stream().map(PersistRole::getName).collect(Collectors.toSet()))),
                        Role.class
        );

        for (PersistRole persistRole : persistRoles) {
            Role role = roles.stream()
                    .filter(r -> r.getNameDescription().getName().equals(persistRole.getName()))
                    .findFirst()
                    .orElse(getRole(persistRole));

            role.getNameDescription().setDescription(persistRole.getDescription());
            role.setPermissions(
                    persistPermissionToPermissionMapper.mapPersistPermissionToPermissionList(persistRole.getPermissions())
            );
        }

        return roles;
    }

    private Role getRole(PersistRole persistRole) {
        Role role = new Role();
        role.setNameDescription(new NameDescription(persistRole.getName(), persistRole.getDescription()));
        return role;
    }
}
