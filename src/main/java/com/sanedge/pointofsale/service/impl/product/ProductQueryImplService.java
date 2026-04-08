package com.sanedge.pointofsale.service.impl.product;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.product.FindAllProductByCategoryRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.api.PaginationMeta;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.repository.product.ProductQueryRepository;
import com.sanedge.pointofsale.service.product.ProductQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQueryImplService implements ProductQueryService {
        private final ProductQueryRepository productQueryRepository;

        @Override
        public ApiResponsePagination<List<ProductResponse>> findAll(FindAllProductRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching products | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<Product> productPage = productQueryRepository.findAllProducts(keyword, pageable);

                List<ProductResponse> responses = productPage.getContent()
                                .stream()
                                .map(ProductResponse::from)
                                .toList();

                log.info("✅ Found {} products", responses.size());

                return ApiResponsePagination.<List<ProductResponse>>builder()
                                .status("success")
                                .message("Products retrieved successfully")
                                .data(responses)
                                .pagination(PaginationMeta.fromSpringPage(productPage))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<ProductResponseDeleteAt>> findActiveProducts(FindAllProductRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching ACTIVE products | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Product> productPage = productQueryRepository.findActiveProducts(keyword, pageable);

                        List<ProductResponseDeleteAt> responses = productPage.getContent()
                                        .stream()
                                        .map(ProductResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active products", responses.size());

                        return ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active products retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(productPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active products", e);
                        return ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active products")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ProductResponseDeleteAt>> findTrashedProducts(FindAllProductRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching TRASHED products | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Product> productPage = productQueryRepository.findTrashedProducts(keyword, pageable);

                        List<ProductResponseDeleteAt> responses = productPage.getContent()
                                        .stream()
                                        .map(ProductResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed products", responses.size());

                        return ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed products retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(productPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed products", e);
                        return ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed products")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ProductResponse>> findByMerchant(FindAllProductByMerchantRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching products by merchant | MerchantID: {}, Page: {}, Size: {}, Search: {}",
                                req.getMerchantId(), page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);

                        Page<Product> productPage = productQueryRepository.findProductsByMerchant(
                                        req.getMerchantId().longValue(),
                                        keyword,
                                        req.getCategoryId().longValue(),
                                        req.getMinPrice(),
                                        req.getMaxPrice(),
                                        pageable);

                        List<ProductResponse> responses = productPage.getContent()
                                        .stream()
                                        .map(ProductResponse::from)
                                        .toList();

                        log.info("✅ Found {} products for merchant {}", responses.size(), req.getMerchantId());

                        return ApiResponsePagination.<List<ProductResponse>>builder()
                                        .status("success")
                                        .message("Products by merchant retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(productPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch products for merchant {}", req.getMerchantId(), e);
                        return ApiResponsePagination.<List<ProductResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch products by merchant")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ProductResponse>> findByCategoryName(FindAllProductByCategoryRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching products by category | Category: {}, Page: {}, Size: {}, Search: {}",
                                req.getCategoryName(), page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);

                        Page<Product> productPage = productQueryRepository.findProductsByCategory(
                                        req.getCategoryName(),
                                        keyword,
                                        req.getMinPrice(),
                                        req.getMaxPrice(),
                                        pageable);

                        List<ProductResponse> responses = productPage.getContent()
                                        .stream()
                                        .map(ProductResponse::from)
                                        .toList();

                        log.info("✅ Found {} products for category {}", responses.size(), req.getCategoryName());

                        return ApiResponsePagination.<List<ProductResponse>>builder()
                                        .status("success")
                                        .message("Products by category retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(productPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch products for category {}", req.getCategoryName(), e);
                        return ApiResponsePagination.<List<ProductResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch products by category")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<ProductResponse> findById(Long productId) {
                log.info("🔍 Fetching product by ID: {}", productId);
                try {
                        return productQueryRepository.findById(productId)
                                        .map(product -> ApiResponse.<ProductResponse>builder()
                                                        .status("success")
                                                        .message("Product retrieved successfully")
                                                        .data(ProductResponse.from(product))
                                                        .build())
                                        .orElseGet(() -> {
                                                log.warn("❌ Product not found with ID: {}", productId);
                                                return ApiResponse.<ProductResponse>builder()
                                                                .status("error")
                                                                .message("Product not found")
                                                                .data(null)
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch product by ID: {}", productId, e);
                        return ApiResponse.<ProductResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch product")
                                        .data(null)
                                        .build();
                }
        }

}
