package com.sanedge.pointofsale.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.pointofsale.security.JwtProvider;

public class MerchantControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldFindAllAndCreateMerchant() throws Exception {
        mockMvc.perform(get("/api/merchant")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "toko")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        CreateMerchantRequest req = new CreateMerchantRequest();
        req.setUserId(adminUser.getUserId().intValue());
        req.setName("ControllerMerchant");
        req.setDescription("Description");
        req.setAddress("Address");
        req.setContactEmail("controller@merchant.com");
        req.setContactPhone("0812345678");
        req.setStatus("PENDING");

        mockMvc.perform(post("/api/merchant/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
