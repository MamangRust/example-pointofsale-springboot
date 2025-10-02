package com.sanedge.pointofsale.domain.requests.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "UpdateMerchantRequest", description = "Request untuk memperbarui merchant")
public class UpdateMerchantRequest {
    @NotNull(message = "merchant_id is required")
    @Schema(description = "ID merchant yang akan diupdate", example = "5001")
    private Integer merchantId;

    @NotNull(message = "user_id is required")
    @Schema(description = "ID user pemilik merchant", example = "1001")
    private Integer userId;

    @NotBlank(message = "name is required")
    @Schema(description = "Nama merchant", example = "Toko Elektronik Modern")
    private String name;

    @NotBlank(message = "description is required")
    @Schema(description = "Deskripsi merchant", example = "Menjual barang-barang elektronik dan gadget terbaru")
    private String description;

    @NotBlank(message = "address is required")
    @Schema(description = "Alamat merchant", example = "Jl. Gatot Subroto No. 20, Jakarta")
    private String address;

    @NotBlank(message = "contact_email is required")
    @Email(message = "contact_email harus format email valid")
    @Schema(description = "Email kontak merchant", example = "merchant_new@example.com")
    private String contactEmail;

    @NotBlank(message = "contact_phone is required")
    @Schema(description = "Nomor telepon merchant", example = "+628987654321")
    private String contactPhone;

    @NotBlank(message = "status is required")
    @Schema(description = "Status merchant", example = "INACTIVE")
    private String status;
}
