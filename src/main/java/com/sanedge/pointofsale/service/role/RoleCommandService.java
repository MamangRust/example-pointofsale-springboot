package com.sanedge.pointofsale.service.role;

import com.sanedge.pointofsale.domain.requests.role.CreateRoleRequest;
import com.sanedge.pointofsale.domain.requests.role.UpdateRoleRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.role.RoleResponse;
import com.sanedge.pointofsale.domain.responses.role.RoleResponseDeleteAt;

public interface RoleCommandService {
    ApiResponse<RoleResponse> create(CreateRoleRequest request);

    ApiResponse<RoleResponse> update(UpdateRoleRequest request);

    ApiResponse<RoleResponseDeleteAt> trash(Integer id);

    ApiResponse<RoleResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
