package com.team1206.pos.authentication;

import com.team1206.pos.user.user.UserRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserRequestDTO user) {
        log.info("Received register request: {}", user);
        return authenticationService.handleRegistration(user);
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody LoginRequestDTO body) {
        log.info("Received login request: {}", body);
        return authenticationService.handleLogin(body);
    }
}
