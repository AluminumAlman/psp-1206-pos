package com.team1206.pos.authentication;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {

    private String email;
    private String password;

}