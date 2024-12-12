package com.team1206.pos.authentication.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${jwt_issuer}")
    private String issuer;


    public String generateToken(String email,
                                String name) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                  .withSubject("User Details")
                  .withClaim("email", email)
                  .withClaim("name", name)
                  .withIssuedAt(new Date())
                  .withIssuer(issuer)
                  .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public String validateTokenAndRetrieveEmail(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }
}
