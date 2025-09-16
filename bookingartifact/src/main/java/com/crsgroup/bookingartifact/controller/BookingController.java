package com.crsgroup.bookingartifact.controller;

import com.crsgroup.bookingartifact.model.Appointment;
import com.crsgroup.bookingartifact.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Appointment createBooking(@RequestParam Long slotId, @RequestParam String patientId) throws Exception {
        return bookingService.createBooking(slotId, patientId);
    }

    @PostMapping("/{id}/cancel")
    public Appointment cancelBooking(@PathVariable Long id) throws Exception {
        return bookingService.cancelBooking(id);
    }
}
