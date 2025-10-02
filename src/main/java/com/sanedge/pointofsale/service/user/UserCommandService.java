package com.sanedge.pointofsale.service.user;

import com.sanedge.pointofsale.domain.requests.user.CreateUserRequest;
import com.sanedge.pointofsale.domain.requests.user.UpdateUserRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponseDeleteAt;

public interface UserCommandService {

    ApiResponse<UserResponse> create(CreateUserRequest req);

    ApiResponse<UserResponse> update(UpdateUserRequest req);

    ApiResponse<UserResponseDeleteAt> trashed(Integer userId);

    ApiResponse<UserResponseDeleteAt> restore(Integer userId);

    ApiResponse<Boolean> deletePermanent(Integer userId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}