package com.crsgroup.paymentartifact.service;

import com.crsgroup.paymentartifact.model.Payment;
import com.crsgroup.paymentartifact.model.OutboxEvent;
import com.crsgroup.paymentartifact.repository.PaymentRepository;
import com.crsgroup.paymentartifact.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    @Transactional
    // public Payment authorize(Long bookingId, Double amount) throws Exception {
    public Payment authorize(Long bookingId, BigDecimal amount) throws Exception {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);

        payment.setAmount(amount);
        // payment.setAmount(BigDecimal.valueOf(amount)); // where amount is double

        payment.setStatus("authorized");
        Payment saved = paymentRepo.save(payment);

        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Payment");
        event.setAggregateId(saved.getId().toString());
        event.setType("payment.authorized");
        event.setPayload(mapper.writeValueAsString(saved));
        outboxRepo.save(event);

        return saved;
    }

    @Transactional
    public Payment capture(Long id) throws Exception {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("captured");
        Payment saved = paymentRepo.save(payment);

        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Payment");
        event.setAggregateId(saved.getId().toString());
        event.setType("payment.completed");
        event.setPayload(mapper.writeValueAsString(saved));
        outboxRepo.save(event);

        return saved;
    }

    @Transactional
    public Payment fail(Long id) throws Exception {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("failed");
        Payment saved = paymentRepo.save(payment);

        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Payment");
        event.setAggregateId(saved.getId().toString());
        event.setType("payment.failed");
        event.setPayload(mapper.writeValueAsString(saved));
        outboxRepo.save(event);

        return saved;
    }
}
