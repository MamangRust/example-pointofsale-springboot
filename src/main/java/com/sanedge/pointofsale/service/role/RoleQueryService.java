package com.sanedge.pointofsale.service.role;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.role.FindAllRoles;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.role.RoleResponse;
import com.sanedge.pointofsale.domain.responses.role.RoleResponseDeleteAt;

public interface RoleQueryService {
    ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req);

    ApiResponse<RoleResponse> findById(Integer id);

    ApiResponse<List<RoleResponse>> findByUserId(Integer user_id);

    ApiResponse<RoleResponse> findByName(String name);
}
