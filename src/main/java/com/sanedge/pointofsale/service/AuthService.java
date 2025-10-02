package com.sanedge.pointofsale.service;

import com.sanedge.pointofsale.domain.requests.auth.AuthRequest;
import com.sanedge.pointofsale.domain.requests.auth.RegisterRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.auth.TokenResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;

public interface AuthService {
    public ApiResponse<TokenResponse> login(AuthRequest loginRequest);

    public ApiResponse<UserResponse> register(RegisterRequest registerRequest);

    public ApiResponse<TokenResponse> refreshToken(String refreshToken);

    public ApiResponse<UserResponse> getCurrentUser();
}
