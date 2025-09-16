package com.crsgroup.bookingartifact.service;

import com.crsgroup.bookingartifact.model.Appointment;
import com.crsgroup.bookingartifact.model.OutboxEvent;
import com.crsgroup.bookingartifact.repository.AppointmentRepository;
import com.crsgroup.bookingartifact.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final AppointmentRepository appointmentRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper; // ✅ injected from Spring, with JavaTimeModule registered

    @Transactional
    public Appointment createBooking(Long slotId, String patientId) throws Exception {
        // prevent double booking
        if (appointmentRepo.existsBySlotIdAndStatus(slotId, "booked")) {
            throw new RuntimeException("Slot already booked!");
        }

        Appointment appt = new Appointment();
        appt.setSlotId(slotId);
        appt.setPatientId(patientId);
        appt.setStatus("booked");

        Appointment saved = appointmentRepo.save(appt);

        // write outbox event
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Appointment");
        event.setAggregateId(saved.getId().toString());
        event.setType("booking.created");
        event.setPayload(mapper.writeValueAsString(saved)); // ✅ now handles LocalDateTime correctly
        outboxRepo.save(event);

        return saved;
    }

    @Transactional
    public Appointment cancelBooking(Long id) throws Exception {
        Appointment appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus("cancelled");
        Appointment saved = appointmentRepo.save(appt);

        // write outbox event
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Appointment");
        event.setAggregateId(saved.getId().toString());
        event.setType("booking.cancelled");
        event.setPayload(mapper.writeValueAsString(saved)); // ✅ no more error
        outboxRepo.save(event);

        return saved;
    }
}
