package com.sanedge.pointofsale.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.product.CreateProductRequest;
import com.sanedge.pointofsale.domain.requests.product.FindAllProductRequest;
import com.sanedge.pointofsale.domain.requests.product.UpdateProductRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.product.ProductResponse;
import com.sanedge.pointofsale.domain.responses.product.ProductResponseDeleteAt;
import com.sanedge.pointofsale.service.product.ProductCommandService;
import com.sanedge.pointofsale.service.product.ProductQueryService;

import jakarta.validation.Validator;

public class ProductServiceIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private FolderService folderService;

    @MockBean
    private FileService fileService;

    @Autowired
    private ProductCommandService commandService;

    @Autowired
    private ProductQueryService queryService;

    @MockBean
    private Validator validator;

    @BeforeEach
    void mockServices() {
        when(folderService.createFolder(any(), any())).thenReturn("static/product/test");
        when(fileService.createFileImage(any(), any())).thenReturn("static/product/test/img.png");
    }

    @Test
    void testAllProductServiceMethods() {
        MockMultipartFile file = new MockMultipartFile("imageProduct", "test-product.png", "image/png", "bytes".getBytes());

        // 1. Create
        CreateProductRequest req = new CreateProductRequest();
        req.setMerchantId(adminMerchant.getMerchantId().intValue());
        req.setCategoryId(1);
        req.setName("Deluxe Monitor X");
        req.setDescription("Premium screen");
        req.setPrice(1200000);
        req.setCountInStock(15);
        req.setBrand("Monitors");
        req.setWeight(2000);
        req.setRating(5);
        req.setSlugProduct("deluxe-monitor-x");
        req.setImageProduct(file);

        ApiResponse<ProductResponse> createResp = commandService.createProduct(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllProductRequest findReq = new FindAllProductRequest();
        findReq.setSearch("Monitor");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<ProductResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<ProductResponse> idResp = queryService.findById(id);
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateProductRequest updateReq = new UpdateProductRequest();
        updateReq.setProductId(id.intValue());
        updateReq.setMerchantId(adminMerchant.getMerchantId().intValue());
        updateReq.setCategoryId(1);
        updateReq.setName("Deluxe Monitor X Updated");
        updateReq.setDescription("Updated Premium screen");
        updateReq.setPrice(1300000);
        updateReq.setCountInStock(10);
        updateReq.setBrand("Monitors");
        updateReq.setWeight(2000);
        updateReq.setRating(5);
        updateReq.setSlugProduct("deluxe-monitor-x-updated");
        updateReq.setImageProduct(file);

        ApiResponse<ProductResponse> updateResp = commandService.updateProduct(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<ProductResponseDeleteAt>> activeResp = queryService.findActiveProducts(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<ProductResponseDeleteAt> trashResp = commandService.trashedProduct(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<ProductResponseDeleteAt>> trashedResp = queryService.findTrashedProducts(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<ProductResponseDeleteAt> restoreResp = commandService.restoreProduct(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent
        ApiResponse<ProductResponseDeleteAt> trashAgainResp = commandService.trashedProduct(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteProductPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllProducts();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllProductsPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
