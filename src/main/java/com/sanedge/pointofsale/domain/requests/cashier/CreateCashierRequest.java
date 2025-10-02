package com.sanedge.pointofsale.domain.requests.cashier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "CreateCashierRequest", description = "Request untuk membuat cashier baru")
public class CreateCashierRequest {
    @NotNull
    @Schema(description = "ID merchant", required = true, example = "123")
    private Integer merchantId;

    @NotNull
    @Schema(description = "ID user", required = true, example = "789")
    private Integer userId;

    @NotBlank
    @Schema(description = "Nama cashier", required = true, example = "John Doe")
    private String name;
}
