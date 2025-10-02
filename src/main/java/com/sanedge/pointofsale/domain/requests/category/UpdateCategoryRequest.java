package com.sanedge.pointofsale.domain.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "UpdateCategoryRequest", description = "Request untuk update kategori")
public class UpdateCategoryRequest {
    @NotNull
    @Schema(description = "ID kategori", required = true, example = "101")
    private Integer categoryId;

    @NotBlank
    @Schema(description = "Nama kategori", required = true, example = "Elektronik Rumah Tangga")
    private String name;

    @NotBlank
    @Schema(description = "Deskripsi kategori", required = true, example = "Produk elektronik rumah tangga")
    private String description;

    @Schema(description = "Slug kategori", example = "elektronik-rumah-tangga")
    private String slugCategory;
}
