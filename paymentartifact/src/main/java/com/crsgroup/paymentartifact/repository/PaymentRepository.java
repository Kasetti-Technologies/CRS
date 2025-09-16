package com.crsgroup.paymentartifact.repository;

import com.crsgroup.paymentartifact.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
