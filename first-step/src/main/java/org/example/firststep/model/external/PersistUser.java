package org.example.firststep.model.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.firststep.model.mongo.entity.user.UserType;

import java.util.List;

@Getter
@NoArgsConstructor
public class PersistUser {
    private String userName;
    private String password;
    private String email;
    private String contactMail;
    private UserType userType;
    private List<String> roles;
    private PersistAddress address;
}

