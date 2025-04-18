package org.example.firststep.controller.api.v0;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.firststep.mapper.PersistRoleToRoleMapper;
import org.example.firststep.mapper.PersistUserToUserMapper;
import org.example.firststep.model.external.PersistRole;
import org.example.firststep.model.external.PersistUser;
import org.example.firststep.model.mongo.entity.user.Role;
import org.example.firststep.model.mongo.entity.user.User;
import org.example.firststep.repository.mongo.user.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v0/security")
@RestController
public class SecurityControllerApi {

    private final MongoTemplate mongoTemplate;
    private final PersistRoleToRoleMapper persistRoleToRoleMapper;

    @PostMapping("/create")
    public ResponseEntity<String> createRoles(@RequestBody List<PersistRole> roles) {

        List<Role> roleList = persistRoleToRoleMapper.mapPersistRoleToRoleList(roles);
        mongoTemplate.save(roleList);

        log.info("User created: \n" + roleList);

        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roleList = mongoTemplate
                .findAll(Role.class, "roles");

        if (roleList.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(roleList);
    }
}
