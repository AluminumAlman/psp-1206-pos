package com.team1206.pos.user.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(201).body(createdUser);
    }

    @GetMapping
    @Operation(summary = "Get user list")
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {
        List<UserResponseDTO> users = userService.getAllUsers(firstname, lastname, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Retrieve user")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID userId) {
        UserResponseDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/merchant")
    @Operation(summary = "Assign merchant to a user")
    public ResponseEntity<UserResponseDTO> assignMerchantToUser(@PathVariable UUID userId, @RequestBody UserMerchantRequestDTO request) {
        UserResponseDTO updatedUser = userService.assignMerchantToUser(userId, request.getMerchantId());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve current user")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UserResponseDTO currentUser = userService.getCurrentUserInfo();
        return ResponseEntity.ok(currentUser);
    }

}
