package com.sanedge.pointofsale.service.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.auth.AuthRequest;
import com.sanedge.pointofsale.domain.requests.auth.RegisterRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.auth.TokenResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.RefreshToken;
import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.repository.refresh_token.RefreshTokenCommandRepository;
import com.sanedge.pointofsale.repository.refresh_token.RefreshTokenQueryRepository;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.security.JwtProvider;
import com.sanedge.pointofsale.security.UserDetailsImpl;
import com.sanedge.pointofsale.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthImplService implements AuthService {

        private AuthenticationManager authenticationManager;
        private UserQueryRepository userQueryRepository;
        private UserCommandRepository userCommandRepository;
        private JwtProvider jwtProvider;
        private PasswordEncoder passwordEncoder;
        private RefreshTokenQueryRepository refreshTokenQueryRepository;
        private RefreshTokenCommandRepository refreshTokenCommandRepository;
        private RoleQueryRepository roleQueryRepository;

        @Autowired
        public AuthImplService(
                        AuthenticationManager authenticationManager,
                        UserQueryRepository userQueryRepository,
                        UserCommandRepository userCommandRepository,
                        JwtProvider jwtProvider,
                        PasswordEncoder passwordEncoder,
                        RefreshTokenQueryRepository refreshTokenQueryRepository,
                        RefreshTokenCommandRepository refreshTokenCommandRepository,
                        RoleQueryRepository roleQueryRepository) {
                this.authenticationManager = authenticationManager;
                this.userQueryRepository = userQueryRepository;
                this.userCommandRepository = userCommandRepository;
                this.jwtProvider = jwtProvider;
                this.passwordEncoder = passwordEncoder;
                this.refreshTokenQueryRepository = refreshTokenQueryRepository;
                this.refreshTokenCommandRepository = refreshTokenCommandRepository;
                this.roleQueryRepository = roleQueryRepository;
        }

        @Override
        public ApiResponse<TokenResponse> login(AuthRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String accessToken = jwtProvider.generateAccessToken(loginRequest.getUsername());
                String refreshTokenStr = jwtProvider.generateRefreshToken(loginRequest.getUsername());

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                User user = userQueryRepository.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Optional<RefreshToken> existingTokenOpt = refreshTokenQueryRepository.findByUserId(user.getUserId());

                RefreshToken refreshTokenEntity;
                if (existingTokenOpt.isPresent()) {
                        refreshTokenEntity = existingTokenOpt.get();
                        refreshTokenEntity.setToken(refreshTokenStr);
                        refreshTokenEntity
                                        .setExpiration(new Timestamp(System.currentTimeMillis()
                                                        + jwtProvider.getJwtRefreshExpirationMs()));
                        refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                } else {
                        refreshTokenCommandRepository.deleteByUserId(user.getUserId());
                        refreshTokenEntity = new RefreshToken();
                        refreshTokenEntity.setUser(user);
                        refreshTokenEntity.setToken(refreshTokenStr);
                        refreshTokenEntity
                                        .setExpiration(new Timestamp(System.currentTimeMillis()
                                                        + jwtProvider.getJwtRefreshExpirationMs()));

                        refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                }

                TokenResponse tokenResponse = TokenResponse.builder()
                                .access_token(accessToken)
                                .refresh_token(refreshTokenEntity.getToken())
                                .build();

                return ApiResponse.<TokenResponse>builder()
                                .status("success")
                                .message("Login successful")
                                .data(tokenResponse)
                                .build();
        }

        @Override
        public ApiResponse<UserResponse> register(RegisterRequest registerRequest) {
                Optional<User> existingUser = userQueryRepository.findByEmail(registerRequest.getEmail());
                if (existingUser.isPresent()) {
                        log.error("❌ [REGISTER] Email already taken | Email: {}", registerRequest.getEmail());
                        throw new IllegalArgumentException("Email already registered");
                }

                Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

                User newUser = new User();
                newUser.setUsername(registerRequest.getUsername());
                newUser.setFirstname(registerRequest.getFirstname());
                newUser.setLastname(registerRequest.getLastname());
                newUser.setEmail(registerRequest.getEmail());
                newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                newUser.setRoles(Set.of(role));

                try {
                        newUser = userCommandRepository.save(newUser);
                } catch (Exception e) {
                        log.error("❌ Failed to create user", e);
                        throw new ResourceNotFoundException("Failed to create user", e);
                }

                UserResponse userResponse = UserResponse.from(newUser);

                return ApiResponse.<UserResponse>builder()
                                .status("success")
                                .message("User registered successfully")
                                .data(userResponse)
                                .build();
        }

        @Override
        public ApiResponse<TokenResponse> refreshToken(String refreshToken) {
                RefreshToken storedToken = refreshTokenQueryRepository.findByToken(refreshToken)
                                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

                if (storedToken.getExpiration().before(new Date())) {
                        log.warn("❌ [REFRESH TOKEN] Token expired | Token: {}", refreshToken);
                        throw new IllegalArgumentException("Refresh token expired");
                }

                User user = storedToken.getUser();

                String newAccessToken = jwtProvider.generateAccessToken(user.getUsername());

                Date newExpiration = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));

                storedToken.setExpiration(new Timestamp(newExpiration.getTime()));

                refreshTokenCommandRepository.save(storedToken);

                TokenResponse tokenResponse = TokenResponse.builder()
                                .access_token(newAccessToken)
                                .refresh_token(refreshToken)
                                .build();

                return ApiResponse.<TokenResponse>builder()
                                .status("success")
                                .message("Access token refreshed successfully")
                                .data(tokenResponse)
                                .build();
        }

        @Override
        public ApiResponse<UserResponse> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                User user = userQueryRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                UserResponse userResponse = UserResponse.from(user);

                return ApiResponse.<UserResponse>builder()
                                .status("success")
                                .message("Current user fetched successfully")
                                .data(userResponse)
                                .build();
        }

}
