package com.rdmishra.backend_tms.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rdmishra.backend_tms.dto.request.RefreshTokenRequest;
import com.rdmishra.backend_tms.dto.request.SingupRequest;
import com.rdmishra.backend_tms.dto.request.UserRequest;
import com.rdmishra.backend_tms.dto.response.TokenRespnose;
import com.rdmishra.backend_tms.dto.response.UserResponse;
import com.rdmishra.backend_tms.model.RefreshToken;
import com.rdmishra.backend_tms.model.User;
import com.rdmishra.backend_tms.repo.RefreshTokenRepo;
import com.rdmishra.backend_tms.repo.UserRepo;
import com.rdmishra.backend_tms.security.CookieServices;
import com.rdmishra.backend_tms.service.AuthService;
import com.rdmishra.backend_tms.service.JWTService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authServices;
    private AuthenticationManager authenticationManager;
    private UserRepo userRepo;
    private JWTService jwtService;
    private ModelMapper mapper;
    private RefreshTokenRepo refreshTokenRepo;
    private CookieServices cookieServices;

    @PostMapping("/login")
    public ResponseEntity<TokenRespnose> login(@RequestBody UserRequest loginrequest, HttpServletResponse response) {
        // authenticate

        Authentication authentication = authenticate(loginrequest);
        User user = userRepo.findByEmail(loginrequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!user.isEnable()) {
            throw new DisabledException("User is disable");
        }

        // genearte -- refresh token
        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOba = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(jwtService.getRefreshTtlseconds()))
                .revoked(false)
                .build();
        refreshTokenRepo.save(refreshTokenOba);
        // generate -- accesstoken
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOba.getJti());

        // cookie opreation
        cookieServices.attatchRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlseconds());
        cookieServices.addNoStoreHeader(response);

        TokenRespnose tokenRespnose = TokenRespnose.of(accessToken, refreshToken, jwtService.getAccessTtlseconds(),
                mapper.map(user, UserResponse.class));
        return ResponseEntity.ok(tokenRespnose);
    }

    private Authentication authenticate(UserRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody SingupRequest signupRequest) {
        UserResponse createdUser = authServices.registerUser(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRespnose> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request) {

        String refreshToken = readRefreshTokenFromRequest(body, request)
                .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }
        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserID(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepo.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("  Refresh Token Revoked");

        }

        if (storedRefreshToken.getExpireAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("  Refresh Token expired");

        }

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("  Refresh Token does not match by this user");

        }

        // refrsh token ko rotate
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setRepalceToken(newJti);
        refreshTokenRepo.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();
        RefreshToken newRefreshToken = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(jwtService.getRefreshTtlseconds()))
                .revoked(false)
                .build();

        refreshTokenRepo.save(newRefreshToken);

        String newAccessTokenOb = jwtService.generateAccessToken(user);
        String newRefreshTokenOb = jwtService.generateRefreshToken(user, newRefreshToken.getJti());

        cookieServices.attatchRefreshCookie(response, newRefreshTokenOb, (int) jwtService.getRefreshTtlseconds());
        cookieServices.addNoStoreHeader(response);

        return ResponseEntity.ok(TokenRespnose.of(newAccessTokenOb, newRefreshTokenOb, jwtService.getAccessTtlseconds(),
                mapper.map(user, UserResponse.class)));

    }

    // this methode will read refresh token from request or body
    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        // 1. Prefres reading refresh token from cokie
        if (request.getCookies() != null) {
            Optional<String> fromCokkie = Arrays.stream(
                    request.getCookies())
                    .filter(c -> cookieServices.getRefreshTokenCookenName().equals(c.getName()))
                    .map(c -> c.getValue())
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCokkie.isPresent()) {
                return fromCokkie;
            }

        }

        // 2.body
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        // 3. custome header
        String refrshHeader = request.getHeader("X-Refresh-Token");
        if (refrshHeader != null && !refrshHeader.isBlank()) {
            return Optional.of(refrshHeader.trim());
        }

        // 4.Authorization= Bearer <>
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer", 0, 0)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {

                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                    // TODO: handle exception
                }
            }
        }

        return Optional.empty();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutSystem(HttpServletRequest request, HttpServletResponse response) {
        readRefreshTokenFromRequest(null, request).ifPresent(token -> {
            try {
                if (jwtService.isRefreshToken(token)) {
                    String jti = jwtService.getJti(token);
                    refreshTokenRepo.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(false);
                        refreshTokenRepo.save(rt);
                    });
                }
            } catch (JwtException ignored) {
                // TODO: handle exception
            }
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
