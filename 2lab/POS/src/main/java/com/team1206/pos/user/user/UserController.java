package com.team1206.pos.user.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //TODO remove this
    /*
    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Received create user request: {}", request);

        UserResponseDTO createdUser = userService.createUser(request);

        log.debug("Returning {} to create user request", createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }*/

    @GetMapping
    @Operation(summary = "Get user list")
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {
        log.info("Received get users request: firstname={} lastname={} email={}", firstname, lastname, email);

        List<UserResponseDTO> users = userService.getAllUsers(firstname, lastname, email);

        log.debug("Returning {} to get users request (firstname={} lastname={} email={})", users, firstname, lastname, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Retrieve user")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID userId) {
        log.info("Received get user request: userId={}", userId);

        UserResponseDTO user = userService.getUserById(userId);

        log.debug("Returning {} to get user request (userId={})", user, userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserUpdateRequestDTO request) {
        log.info("Received update user request: userId={} {}", userId, request);

        UserResponseDTO updatedUser = userService.updateUser(userId, request);

        log.debug("Returning {} to update user request (userId={})", updatedUser, userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        log.info("Received delete user request: userId={}", userId);

        userService.deleteUser(userId);

        log.debug("Returning nothing to delete user request (userId={})", userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/merchant")
    @Operation(summary = "Assign merchant to a user")
    public ResponseEntity<UserResponseDTO> assignMerchantToUser(@PathVariable UUID userId, @RequestBody UserMerchantRequestDTO request) {
        log.info("Received assign merchant to user request: userId={} {}", userId, request);

        UserResponseDTO updatedUser = userService.assignMerchantToUser(userId, request.getMerchantId());

        log.debug("Returning {} to assign merchant to user request (userId={})", updatedUser, userId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve current user")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        log.info("Received get current user request");

        UserResponseDTO currentUser = userService.getCurrentUserInfo();

        log.debug("Returning {} to get current user request", currentUser);
        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "switch merchant for super-admins")
    @PatchMapping("/switch-merchant")
    public ResponseEntity<UserResponseDTO> switchMerchant(@RequestParam(required = false) UUID merchantId) {
        log.info("Received switch merchant request: merchantId={}", merchantId);

        UserResponseDTO updatedUser = userService.switchMerchant(merchantId);

        log.debug("Returning {} to switch merchant request (merchantId={})", updatedUser, merchantId);
        return ResponseEntity.ok(updatedUser);
    }

}
