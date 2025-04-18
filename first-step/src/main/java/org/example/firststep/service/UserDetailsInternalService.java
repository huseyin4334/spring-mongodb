package org.example.firststep.service;

import lombok.RequiredArgsConstructor;
import org.example.firststep.model.mongo.entity.user.Permission;
import org.example.firststep.model.mongo.entity.user.Role;
import org.example.firststep.model.mongo.entity.user.User;
import org.example.firststep.repository.mongo.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsInternalService implements UserDetailsService {

    private final UserRepository userRepository;

    private static final String ROLE_PREFIX = "ROLE_";

    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUserName())
                .password(passwordEncoder.encode(user.getPassword()))
                .authorities(getAuthorities(user.getRoles()))
                .build();
    }

    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add((GrantedAuthority) () -> ROLE_PREFIX + role.getNameDescription().getName());
            for (Permission permission : role.getPermissions()) {
                authorities.add((GrantedAuthority) () -> permission.getNameDescription().getName());
            }
        }

        return authorities;
    }
}
