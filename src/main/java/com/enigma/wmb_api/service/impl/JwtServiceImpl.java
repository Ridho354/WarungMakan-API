package com.enigma.wmb_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.response.JwtClaims;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

// #SPRING SECURITY# 08 buat JWTService
@Service
public class JwtServiceImpl implements JwtService {
    private final String JWT_SECRET;
    private final Long JWT_EXPIRATION;
    private final String ISSUER;
    private final Algorithm algorithm;

    public JwtServiceImpl(
            @Value("${wmb_api.jwt.secret_key}") String jwtSecret,
            @Value("${wmb_api.jwt.expirationInSecond}") Long jwtExpiration,
            @Value("${wmb_api.jwt.issuer}") String issuer) {
        JWT_SECRET = jwtSecret;
        JWT_EXPIRATION = jwtExpiration;
        ISSUER = issuer;
        algorithm = Algorithm.HMAC512(jwtSecret);
    }


    // #SPRING SECURITY# 08b buat method generateToken, simpelnya bisa dibilang mapping userAccount ke token
    // inget builder pattern
    @Override
    public String generateToken(UserAccount userAccount) {
        try {
            // builder patternya menggunakan JWT.create().sign()
            return JWT.create()
                    .withSubject(String.valueOf(userAccount.getId())) // ini di payload jwt akan seperti ini "sub": "bf9c285a-608a-49d4-9c82-92dcebef95a9"
                    .withIssuedAt(Instant.now()) // ini di payload jwt akan "iat": 1732848422, unix timestamp  atau setara dengan GMT Friday, November 29, 2024 2:47:02 AM https://www.epochconverter.com/
                    .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION)) // "exp": 1732850222 lebih lama misal 30 menit dari token
                    // parameter kedua butuh list of string jadi perlu di mapping seperti (role) -> role.getAuthority()
                    // di payload akan seperti ini
                    /*
                    * "roles": [
                        "CUSTOMER"
                      ],*/
                    .withClaim("roles", userAccount.getAuthorities().stream().map((role) -> role.getAuthority()).toList())
                    .withIssuer(ISSUER) // "iss": "Warung_Makan_Bahari_API"
                    .sign(algorithm); // Algorithm.HMAC512(jwtSecret);
        } catch (JWTCreationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constant.ERROR_CREATING_JWT);
        }
    }

    // #SPRING SECURITY# 09b buat untuk verifikasi jwt / token
    @Override
    public boolean verifyJwtToken(String bearerToken) {
        // buat jwtVerifier sebagai alat untuk ngeverify
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            String jwtToken = extractJwtFromBearerToken(bearerToken);
            jwtVerifier.verify(jwtToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private String extractJwtFromBearerToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return "";
    }

    @Override
    public String getUserIdByToken(String bearerToken) {
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            String jwtToken = extractJwtFromBearerToken(bearerToken);
            DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);
            return decodedJWT.getSubject(); // subject itu adalah userAccountId
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
