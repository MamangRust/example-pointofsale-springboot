package com.sanedge.pointofsale.domain.requests.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk update role")
public class UpdateRoleRequest {

    @Min(value = 1, message = "ID role minimal 1")
    private Integer id;

    @NotBlank(message = "Nama role wajib diisi")
    private String name;
}
