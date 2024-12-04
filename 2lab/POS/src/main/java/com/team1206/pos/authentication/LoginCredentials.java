package com.team1206.pos.authentication;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginCredentials {

    private String email;
    private String password;

}