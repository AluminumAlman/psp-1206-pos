package com.team1206.pos.authentication;

import com.team1206.pos.authentication.security.JWTUtil;
import com.team1206.pos.user.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserService userService,
                                 JWTUtil jwtUtil,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> handleRegistration(UserRequestDTO user) {
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
        UserResponseDTO response = userService.createUser(user);
        String token = jwtUtil.generateToken(response.getEmail(),
                                             response.getFirstName() + " " + response.getLastName());
        return Collections.singletonMap("jwt-token", token);
    }

    public Map<String, Object> handleLogin(LoginRequestDTO body) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

        authenticationManager.authenticate(authInputToken);

        UserResponseDTO user = userService.getUserByEmail(body.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(),
                                             user.getFirstName() + " " + user.getLastName());

        return Collections.singletonMap("jwt-token", token);
    }
}
