package com.sanedge.pointofsale.service.product;

import com.sanedge.pointofsale.domain.requests.product.CreateProductRequest;
import com.sanedge.pointofsale.domain.requests.product.UpdateProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;

public interface ProductCommandService {
    ApiResponse<ProductResponse> createProduct(CreateProductRequest req);

    ApiResponse<ProductResponse> updateProduct(UpdateProductRequest req);

    ApiResponse<ProductResponseDeleteAt> trashedProduct(Integer productId);

    ApiResponse<ProductResponseDeleteAt> restoreProduct(Integer productId);

    ApiResponse<Boolean> deleteProductPermanent(Integer productId);

    ApiResponse<Boolean> restoreAllProducts();

    ApiResponse<Boolean> deleteAllProductsPermanent();
}
