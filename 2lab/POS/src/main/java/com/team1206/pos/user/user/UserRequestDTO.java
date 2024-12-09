package com.team1206.pos.user.user;

import com.team1206.pos.common.enums.UserRoles;
import jakarta.validation.constraints.*;
import lombok.Data;

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

    @NotNull(message = "A role must be assigned")
    private UserRoles role;
}
