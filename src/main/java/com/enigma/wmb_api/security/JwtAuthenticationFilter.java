package com.enigma.wmb_api.security;

import com.enigma.wmb_api.dto.response.JwtClaims;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
* // #SPRING SECURITY# 09 ini untuk kasus selain endpoint login dan register, kasus untuk mengamankan endpoints dari user belum login
* belum login (tidak memiliki token yang valid)
* setiap request selain auth (login register) akan masuk doFilterInternal (selain yang di permitAll, seperti auth -> auth.requestMatchers("/api/v1/auth/**").permitAll())
* kita akan dapetin Authorization Header
* */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final static String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String bearerTokenFromAuthHeader = request.getHeader(AUTH_HEADER);
            // di cek tokennya apakah signaturenya sesuai dan masih belum expired
            if (bearerTokenFromAuthHeader != null && jwtService.verifyJwtToken(bearerTokenFromAuthHeader)) {
                // kita butuh decode dari tokennya supaya kita dapat userAccountId
                String userId = jwtService.getUserIdByToken(bearerTokenFromAuthHeader); // decode jwt dan dapetin userIdnya
                UserAccount userAccount = userService.getByUserId(userId);

                if (userAccount != null) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAccount, null, userAccount.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetails(request)); // optional untuk menambahkan details

                    // lalu kita simpan ke tempat penyimpanan global untuk urusan keamanan/security di SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken); // akhirnya lolos .anyRequest().authenticated()
                }
                // sama seperti login, kita perlu buat AuthenticationToken

            }
        } catch (Exception e) {
            System.out.println("ERROR DI JWTAUTHENTICATIONFILTER");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
