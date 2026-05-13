package com.sanedge.pointofsale.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.security.JwtProvider;
import com.sanedge.pointofsale.service.FileService;
import com.sanedge.pointofsale.service.FolderService;

public class ProductControllerIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    private FolderService folderService;

    @MockitoBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
        when(folderService.createFolder(any(), any())).thenReturn("static/product/test");
        when(fileService.createFileImage(any(), any())).thenReturn("static/product/test/img.png");
    }

    @Test
    void shouldFindAllProductsAndCreate() throws Exception {
        Product product = new Product();
        product.setName("Ultimate Mechanical Keyboard");
        product.setSlugProduct("ultimate-keyboard");
        product.setDescription("RGB gaming keyboard");
        product.setPrice(150000);
        product.setCountInStock(20);
        product.setBrand("Gaming Gear");
        product.setWeight(1200);
        product.setCategoryId(1L);
        product.setMerchantId(adminMerchant.getMerchantId());
        productCommandRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/product")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "Mechanical")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test creation via multipart post
        MockMultipartFile file = new MockMultipartFile("imageProduct", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(multipart("/api/product/create")
                .file(file)
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", String.valueOf(adminMerchant.getMerchantId()))
                .param("categoryId", "1")
                .param("name", "RGB Gaming Mouse")
                .param("description", "High precision mouse")
                .param("price", "500000")
                .param("countInStock", "10")
                .param("brand", "Logi")
                .param("weight", "150")
                .param("rating", "4")
                .param("slugProduct", "rgb-gaming-mouse")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
