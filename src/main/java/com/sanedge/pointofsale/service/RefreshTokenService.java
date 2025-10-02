package com.sanedge.pointofsale.service;

import java.util.Optional;

import com.sanedge.pointofsale.models.RefreshToken;
import com.sanedge.pointofsale.models.User;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findyByUser(User user);

    int deleteByUserId(Long userId);

    RefreshToken updateExpiratyDate(RefreshToken refreshToken);
}
