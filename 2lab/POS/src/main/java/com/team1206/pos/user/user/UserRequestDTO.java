package com.team1206.pos.user.user;

import com.team1206.pos.user.user.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 40)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 40)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 255)
    private String password;

    @NotEmpty(message = "At least one role must be assigned")
    private List<Role> roles;

    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;
}
