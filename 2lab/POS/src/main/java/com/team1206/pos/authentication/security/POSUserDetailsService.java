package com.team1206.pos.authentication.security;

import com.team1206.pos.enums.ResourceType;
import com.team1206.pos.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class POSUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public POSUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static List<GrantedAuthority> mapRoleToAuthorities(UserRoles role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Optional<User> userRes = userRepository.findByEmail(email);
        if (userRes.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.USER, email);
        }

        User user = userRes.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                                                                      user.getPassword(),
                                                                      mapRoleToAuthorities(user.getRole()));
    }
}
