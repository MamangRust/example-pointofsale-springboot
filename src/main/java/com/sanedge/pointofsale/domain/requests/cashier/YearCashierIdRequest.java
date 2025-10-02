package com.sanedge.pointofsale.domain.requests.cashier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "YearCashierIdRequest", description = "Request untuk laporan tahunan kasir berdasarkan cashier_id dan tahun")
public class YearCashierIdRequest {

    @NotNull(message = "cashier_id is required")
    @Schema(description = "ID kasir", example = "101", required = true)
    private Integer cashierId;

    @NotNull(message = "year is required")
    @Schema(description = "Tahun laporan", example = "2025", required = true)
    private Integer year;
}