package com.sanedge.pointofsale.service.impl.product;

import java.io.File;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.product.CreateProductRequest;
import com.sanedge.pointofsale.domain.requests.product.UpdateProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.repository.merchant.MerchantQueryRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.repository.product.ProductQueryRepository;
import com.sanedge.pointofsale.service.FileService;
import com.sanedge.pointofsale.service.FolderService;
import com.sanedge.pointofsale.service.product.ProductCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductCommandImplService implements ProductCommandService {
    private final ProductCommandRepository productCommandRepository;
    private final ProductQueryRepository productQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final Validator validator;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String PRODUCT_BASE_PATH = "static/product";

    @Override
    public ApiResponse<ProductResponse> createProduct(CreateProductRequest req) {
        log.info("🆕 Creating product: {}", req.getName());
        try {
            validateRequest(req);

            String folderPath = folderService.createFolder(PRODUCT_BASE_PATH, req.getSlugProduct());
            if (folderPath == null) {
                return ApiResponse.<ProductResponse>builder()
                        .status("error")
                        .message("Failed to create folder for product")
                        .data(null)
                        .build();
            }

            String filePath = folderPath + File.separator + req.getImageProduct().getOriginalFilename();
            String savedPath = fileService.createFileImage(req.getImageProduct(), filePath);
            if (savedPath == null) {
                return ApiResponse.<ProductResponse>builder()
                        .status("error")
                        .message("Failed to save product image")
                        .data(null)
                        .build();
            }

            Product product = new Product();
            product.setMerchantId(req.getMerchantId().longValue());
            product.setCategoryId(req.getCategoryId().longValue());
            product.setName(req.getName());
            product.setDescription(req.getDescription());
            product.setPrice(req.getPrice());
            product.setCountInStock(req.getCountInStock());
            product.setBrand(req.getBrand());
            product.setWeight(req.getWeight());
            product.setSlugProduct(req.getSlugProduct());
            product.setImageProduct(savedPath);

            Product saved = productCommandRepository.save(product);

            return ApiResponse.<ProductResponse>builder()
                    .status("success")
                    .message("Product created successfully")
                    .data(ProductResponse.from(saved))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create product: {}", req.getName(), e);
            return ApiResponse.<ProductResponse>builder()
                    .status("error")
                    .message("Failed to create product")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ProductResponse> updateProduct(UpdateProductRequest req) {
        log.info("✏️ Updating product ID: {}", req.getProductId());
        try {
            validateRequest(req);

            if (merchantQueryRepository.findMerchantById(req.getMerchantId().longValue()).isEmpty()) {
                return ApiResponse.<ProductResponse>builder()
                        .status("error")
                        .message("Product not found with id " + req.getMerchantId())
                        .data(null)
                        .build();
            }

            Product product = productQueryRepository.findProductById(req.getProductId().longValue())
                    .orElse(null);
            if (product == null) {
                return ApiResponse.<ProductResponse>builder()
                        .status("error")
                        .message("Product not found")
                        .data(null)
                        .build();
            }

            if (req.getImageProduct() != null) {
                if (product.getImageProduct() != null) {
                    fileService.deleteFileImage(product.getImageProduct());
                }
                String folderPath = folderService.createFolder(PRODUCT_BASE_PATH, req.getSlugProduct());
                String filePath = folderPath + File.separator + req.getImageProduct().getOriginalFilename();
                String savedPath = fileService.createFileImage(req.getImageProduct(), filePath);
                product.setImageProduct(savedPath);
            }

            product.setMerchantId(req.getMerchantId().longValue());
            product.setCategoryId(req.getCategoryId().longValue());
            product.setName(req.getName());
            product.setDescription(req.getDescription());
            product.setPrice(req.getPrice());
            product.setCountInStock(req.getCountInStock());
            product.setBrand(req.getBrand());
            product.setWeight(req.getWeight());
            product.setSlugProduct(req.getSlugProduct());

            Product updated = productCommandRepository.save(product);

            return ApiResponse.<ProductResponse>builder()
                    .status("success")
                    .message("Product updated successfully")
                    .data(ProductResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update product ID: {}", req.getProductId(), e);
            return ApiResponse.<ProductResponse>builder()
                    .status("error")
                    .message("Failed to update product")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ProductResponseDeleteAt> trashedProduct(Integer productId) {
        log.info("🗑️ Trashing product ID: {}", productId);
        try {
            Product product = productCommandRepository.trashed(productId.longValue());
            return ApiResponse.<ProductResponseDeleteAt>builder()
                    .status("success")
                    .message("Product trashed successfully")
                    .data(ProductResponseDeleteAt.from(product))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash product ID: {}", productId, e);
            return ApiResponse.<ProductResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash product")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ProductResponseDeleteAt> restoreProduct(Integer productId) {
        log.info("♻️ Restoring product ID: {}", productId);
        try {
            Product product = productCommandRepository.restore(productId.longValue());
            return ApiResponse.<ProductResponseDeleteAt>builder()
                    .status("success")
                    .message("Product restored successfully")
                    .data(ProductResponseDeleteAt.from(product))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore product ID: {}", productId, e);
            return ApiResponse.<ProductResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore product")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteProductPermanent(Integer productId) {
        log.info("🧨 Permanently deleting product ID: {}", productId);
        try {
            productCommandRepository.deletePermanent(productId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Product permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete product ID: {}", productId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete product")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllProducts() {
        log.info("🔄 Restoring ALL trashed products");
        try {
            productCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All products restored successfully")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all products", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all products")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllProductsPermanent() {
        log.info("💣 Permanently deleting ALL trashed products");
        try {
            productCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All products permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all products", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all products")
                    .data(false)
                    .build();
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
