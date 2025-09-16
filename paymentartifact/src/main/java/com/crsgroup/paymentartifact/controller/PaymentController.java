package com.crsgroup.paymentartifact.controller;

import com.crsgroup.paymentartifact.model.Payment;
import com.crsgroup.paymentartifact.service.PaymentService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // @PostMapping("/authorize")
    // public Payment authorize(@RequestParam Long bookingId, @RequestParam Double amount) throws Exception {
    //     return paymentService.authorize(bookingId, amount);
    // }

    @PostMapping("/authorize")
    public Payment authorize(@RequestParam Long bookingId, @RequestParam BigDecimal amount) throws Exception {
        return paymentService.authorize(bookingId, amount);
    }


    @PostMapping("/{id}/capture")
    public Payment capture(@PathVariable Long id) throws Exception {
        return paymentService.capture(id);
    }

    @PostMapping("/{id}/fail")
    public Payment fail(@PathVariable Long id) throws Exception {
        return paymentService.fail(id);
    }
}
