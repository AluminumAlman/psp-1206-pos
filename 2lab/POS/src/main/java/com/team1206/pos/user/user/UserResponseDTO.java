package com.team1206.pos.user.user;

import com.team1206.pos.user.user.User.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Role> roles;
    private UUID merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
