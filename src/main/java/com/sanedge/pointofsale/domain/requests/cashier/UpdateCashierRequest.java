package com.sanedge.pointofsale.domain.requests.cashier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "UpdateCashierRequest", description = "Request untuk update cashier")
public class UpdateCashierRequest {
    @NotNull
    @Schema(description = "ID cashier", required = true, example = "456")
    private Integer cashierId;

    @NotBlank
    @Schema(description = "Nama cashier", required = true, example = "Jane Doe")
    private String name;
}
