package com.omnicharge.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnicharge.payment.dto.PaymentDtos.*;
import com.omnicharge.payment.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentResponse mockPaymentResponse;
    private TransactionResponse mockTransactionResponse;

    @BeforeEach
    void setUp() {
        mockPaymentResponse = PaymentResponse.builder()
                .transactionId("TXN-123")
                .status("SUCCESS")
                .message("Payment processed successfully")
                .build();

        mockTransactionResponse = TransactionResponse.builder()
                .id(1L)
                .transactionId("TXN-123")
                .rechargeId("RCH-123")
                .userId(10L)
                .amount(BigDecimal.valueOf(299.0))
                .status("SUCCESS")
                .build();
    }

    @Test
    void processPayment_ValidRequest_ReturnsOk() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId("RCH-123");
        request.setUserId(10L);
        request.setAmount(BigDecimal.valueOf(299.0));
        request.setDescription("Test Recharge");

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(mockPaymentResponse);

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN-123"));
    }

    @Test
    void getTransaction_ValidId_ReturnsOk() throws Exception {
        when(paymentService.getTransactionById(anyString())).thenReturn(mockTransactionResponse);

        mockMvc.perform(get("/api/payments/transaction/TXN-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN-123"));
    }

    @Test
    void getByRechargeId_ValidId_ReturnsOk() throws Exception {
        when(paymentService.getTransactionByRechargeId(anyString())).thenReturn(mockTransactionResponse);

        mockMvc.perform(get("/api/payments/recharge/RCH-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rechargeId").value("RCH-123"));
    }

    @Test
    void getUserTransactions_ValidUserId_ReturnsOk() throws Exception {
        when(paymentService.getTransactionsByUser(anyLong())).thenReturn(List.of(mockTransactionResponse));

        mockMvc.perform(get("/api/payments/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    void getAllTransactions_ReturnsOk() throws Exception {
        when(paymentService.getAllTransactions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/payments/admin/all"))
                .andExpect(status().isOk());
    }
}
