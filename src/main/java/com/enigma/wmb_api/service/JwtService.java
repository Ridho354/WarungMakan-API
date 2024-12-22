package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.response.JwtClaims;
import com.enigma.wmb_api.entity.UserAccount;

public interface JwtService {
    String generateToken(UserAccount userAccount);
    // bearerToken i.e. "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiZjljMjg1YS02MDhhLTQ5ZDQtOWM4Mi05MmRjZWJlZjk1YTkiLCJpYXQiOjE3MzI4NTIwMTIsImV4cCI6MTczMjg1MzgxMiwicm9sZXMiOlsiQ1VTVE9NRVIiXSwiaXNzIjoiV2FydW5nX01ha2FuX0JhaGFyaV9BUEkifQ.uCxkl8cLh7ORTU0r8B6o0YGdhuGVwXQmyLn0keVVCpOD8EJwA_AeEF5AcCW1QIt_n-1yqaYbUUdmzPlRS1m9_Q"
    boolean verifyJwtToken(String bearerToken);
    String getUserIdByToken(String bearerTokenFromAuthHeader);
}
