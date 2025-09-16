package com.crsgroup.paymentartifact.service;

import com.crsgroup.paymentartifact.model.Payment;
import com.crsgroup.paymentartifact.model.OutboxEvent;
import com.crsgroup.paymentartifact.repository.PaymentRepository;
import com.crsgroup.paymentartifact.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepo;

    @Mock
    private OutboxRepository outboxRepo;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthorize_success() throws Exception {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBookingId(101L);
        payment.setAmount(BigDecimal.valueOf(500));
        payment.setStatus("authorized");

        when(paymentRepo.save(any(Payment.class))).thenReturn(payment);
        when(mapper.writeValueAsString(any(Payment.class))).thenReturn("{json}");

        Payment result = paymentService.authorize(101L, BigDecimal.valueOf(500));

        assertNotNull(result);
        assertEquals("authorized", result.getStatus());
        assertEquals(101L, result.getBookingId());
        verify(outboxRepo, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testCapture_success() throws Exception {
        Payment payment = new Payment();
        payment.setId(2L);
        payment.setBookingId(202L);
        payment.setAmount(BigDecimal.valueOf(750));
        payment.setStatus("authorized");

        when(paymentRepo.findById(2L)).thenReturn(Optional.of(payment));
        when(paymentRepo.save(any(Payment.class))).thenReturn(payment);
        when(mapper.writeValueAsString(any(Payment.class))).thenReturn("{json}");

        Payment result = paymentService.capture(2L);

        assertEquals("captured", result.getStatus());
        verify(outboxRepo, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testCapture_notFound() {
        when(paymentRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.capture(999L));

        assertEquals("Payment not found", ex.getMessage());
        verify(outboxRepo, never()).save(any());
    }

    @Test
    void testFail_success() throws Exception {
        Payment payment = new Payment();
        payment.setId(3L);
        payment.setBookingId(303L);
        payment.setAmount(BigDecimal.valueOf(250));
        payment.setStatus("authorized");

        when(paymentRepo.findById(3L)).thenReturn(Optional.of(payment));
        when(paymentRepo.save(any(Payment.class))).thenReturn(payment);
        when(mapper.writeValueAsString(any(Payment.class))).thenReturn("{json}");

        Payment result = paymentService.fail(3L);

        assertEquals("failed", result.getStatus());
        verify(outboxRepo, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testFail_notFound() {
        when(paymentRepo.findById(888L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.fail(888L));

        assertEquals("Payment not found", ex.getMessage());
        verify(outboxRepo, never()).save(any());
    }
}
