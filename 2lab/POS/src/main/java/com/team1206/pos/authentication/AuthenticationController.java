package com.team1206.pos.authentication;

import com.team1206.pos.user.user.UserRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserRequestDTO user) {
        return authenticationService.registrationHandler(user);
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody LoginRequestDTO body) {
        return authenticationService.loginHandler(body);
    }
}
