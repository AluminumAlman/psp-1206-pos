package com.team1206.pos.authentication;

import com.team1206.pos.authentication.security.JWTUtil;
import com.team1206.pos.exceptions.UserNotFoundException;
import com.team1206.pos.user.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private UserRepository userRepository;
    private JWTUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    public AuthenticationController(UserService userService,
                                    UserRepository userRepository,
                                    JWTUtil jwtUtil,
                                    AuthenticationManager authenticationManager,
                                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserRequestDTO user) {
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
        UserResponseDTO response = userService.createUser(user);
        String token = jwtUtil.generateToken(response.getEmail(),
                                             response.getFirstName() + " " + response.getLastName());
        return Collections.singletonMap("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody LoginCredentials body) {
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

            authenticationManager.authenticate(authInputToken);

            User user = userRepository.findByEmail(body.getEmail())
                                      .orElseThrow(() -> new UserNotFoundException(body.getEmail()));

            String token = jwtUtil.generateToken(user.getEmail(),
                                                 user.getFirstName() + " " + user.getLastName());

            return Collections.singletonMap("jwt-token", token);
        }
        catch (AuthenticationException authExc) {
            throw new RuntimeException("Invalid Login Credentials");
        }
    }
}