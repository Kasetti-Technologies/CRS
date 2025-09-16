package com.crsgroup.paymentartifact.controller;

import com.crsgroup.paymentartifact.model.Payment;
import com.crsgroup.paymentartifact.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void testAuthorize() throws Exception {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBookingId(10L);
        payment.setAmount(BigDecimal.valueOf(500));
        payment.setStatus("authorized");

        when(paymentService.authorize(10L, BigDecimal.valueOf(500))).thenReturn(payment);

        mockMvc.perform(post("/payments/authorize")
                        .param("bookingId", "10")
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.status").value("authorized"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void testCapture() throws Exception {
        Payment payment = new Payment();
        payment.setId(2L);
        payment.setBookingId(20L);
        payment.setAmount(BigDecimal.valueOf(800));
        payment.setStatus("captured");

        when(paymentService.capture(2L)).thenReturn(payment);

        mockMvc.perform(post("/payments/2/capture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("captured"))
                .andExpect(jsonPath("$.bookingId").value(20));
    }

    @Test
    void testFail() throws Exception {
        Payment payment = new Payment();
        payment.setId(3L);
        payment.setBookingId(30L);
        payment.setAmount(BigDecimal.valueOf(300));
        payment.setStatus("failed");

        when(paymentService.fail(3L)).thenReturn(payment);

        mockMvc.perform(post("/payments/3/fail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.bookingId").value(30));
    }
}
