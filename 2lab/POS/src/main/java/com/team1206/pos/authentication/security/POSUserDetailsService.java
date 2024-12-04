package com.team1206.pos.authentication.security;

import com.team1206.pos.exceptions.UserNotFoundException;
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
import java.util.stream.Collectors;

@Service
public class POSUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public POSUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static List<GrantedAuthority> mapRolesToAuthorities(List<User.Role> roles) {
        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Optional<User> userRes = userRepository.findByEmail(email);
        if (userRes.isEmpty()) {
            throw new UserNotFoundException(email);
        }

        User user = userRes.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                                                                      user.getPassword(),
                                                                      mapRolesToAuthorities(user.getRoles()));
    }
}
