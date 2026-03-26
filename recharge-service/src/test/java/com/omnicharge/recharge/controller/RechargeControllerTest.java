package com.omnicharge.recharge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnicharge.recharge.dto.RechargeDtos.RechargeRequest;
import com.omnicharge.recharge.dto.RechargeDtos.RechargeResponse;
import com.omnicharge.recharge.security.JwtUtil;
import com.omnicharge.recharge.service.RechargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RechargeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RechargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RechargeService rechargeService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private RechargeResponse mockResponse;
    private RechargeRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockResponse = RechargeResponse.builder()
                .id(1L)
                .rechargeId("RCH-123")
                .mobileNumber("9876543210")
                .operatorName("Airtel")
                .planName("Unlimited 299")
                .amount(BigDecimal.valueOf(299.0))
                .validityDays(28)
                .status("SUCCESS")
                .transactionId("TXN-123")
                .build();

        mockRequest = new RechargeRequest();
        mockRequest.setMobileNumber("9876543210");
        mockRequest.setOperatorId(1L);
        mockRequest.setPlanId(1L);
    }

    @Test
    void initiateRecharge_ValidRequest_ReturnsCreated() throws Exception {
        when(jwtUtil.extractUserId(anyString())).thenReturn(10L);
        when(rechargeService.initiateRecharge(anyLong(), any(RechargeRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/recharges")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rechargeId").value("RCH-123"));
    }

    @Test
    void getHistory_ValidRequest_ReturnsOk() throws Exception {
        when(jwtUtil.extractUserId(anyString())).thenReturn(10L);
        when(rechargeService.getRechargeHistory(anyLong())).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/recharges/history")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rechargeId").value("RCH-123"));
    }

    @Test
    void getHistoryByUserId_ValidUserId_ReturnsOk() throws Exception {
        when(rechargeService.getRechargeHistory(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recharges/history/user/10")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk());
    }

    @Test
    void getByRechargeId_ValidId_ReturnsOk() throws Exception {
        when(rechargeService.getRechargeByRechargeId("RCH-123")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/recharges/RCH-123")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rechargeId").value("RCH-123"));
    }

    @Test
    void getById_ValidId_ReturnsOk() throws Exception {
        when(rechargeService.getRechargeById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/recharges/id/1")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rechargeId").value("RCH-123"));
    }
}
