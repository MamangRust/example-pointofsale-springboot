package com.sanedge.pointofsale.service.product;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.product.FindAllProductByCategoryRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;

public interface ProductQueryService {
    ApiResponsePagination<List<ProductResponse>> findAll(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponseDeleteAt>> findActiveProducts(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponseDeleteAt>> findTrashedProducts(FindAllProductRequest req);

    ApiResponsePagination<List<ProductResponse>> findByMerchant(FindAllProductByMerchantRequest req);

    ApiResponsePagination<List<ProductResponse>> findByCategoryName(FindAllProductByCategoryRequest req);

    ApiResponse<ProductResponse> findById(Long productId);

}
