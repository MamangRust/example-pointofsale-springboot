package com.sanedge.pointofsale.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.auth.AuthRequest;
import com.sanedge.pointofsale.domain.requests.auth.RegisterRequest;
import com.sanedge.pointofsale.domain.requests.refresh_token.RefreshTokenRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.auth.TokenResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest req) {
        ApiResponse<UserResponse> response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody AuthRequest req) {
        ApiResponse<TokenResponse> response = authService.login(req);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/healthchecker")
    public ResponseEntity<String> healthChecker() {
        return ResponseEntity.ok("✅ API is up and running!");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        ApiResponse<UserResponse> response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestBody RefreshTokenRequest req) {
        ApiResponse<TokenResponse> response = authService.refreshToken(req.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
