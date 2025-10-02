package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.product.CreateProductRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductByCategoryRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductRequest;
import com.sanedge.pointofsale.domain.requests.product.UpdateProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;
import com.sanedge.pointofsale.service.product.ProductCommandService;
import com.sanedge.pointofsale.service.product.ProductQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductQueryService queryService;
    private final ProductCommandService commandService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findAll(
            @ModelAttribute FindAllProductRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/merchant/{merchant_id}")
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findByMerchant(
            @PathVariable("merchant_id") Integer merchantId,
            @ModelAttribute FindAllProductByMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByMerchant(req));
    }

    @GetMapping("/category/{category_name}")
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findByCategory(
            @PathVariable("category_name") String categoryName,
            @ModelAttribute FindAllProductByCategoryRequest req) {
        return ResponseEntity.ok(queryService.findByCategoryName(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<ProductResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllProductRequest req) {
        return ResponseEntity.ok(queryService.findActiveProducts(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<ProductResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllProductRequest req) {

        return ResponseEntity.ok(queryService.findTrashedProducts(req));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @ModelAttribute CreateProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.createProduct(req));
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateProductRequest req) {
        req.setProductId(id);
        return ResponseEntity.ok(commandService.updateProduct(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDeleteAt>> trashedProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trashedProduct(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDeleteAt>> restoreProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restoreProduct(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProductPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.deleteProductPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllProducts() {
        return ResponseEntity.ok(commandService.restoreAllProducts());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllProductsPermanent() {
        return ResponseEntity.ok(commandService.deleteAllProductsPermanent());
    }
}
