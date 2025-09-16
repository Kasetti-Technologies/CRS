package com.crsgroup.bookingartifact.service;

import com.crsgroup.bookingartifact.model.Appointment;
import com.crsgroup.bookingartifact.model.OutboxEvent;
import com.crsgroup.bookingartifact.repository.AppointmentRepository;
import com.crsgroup.bookingartifact.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private OutboxRepository outboxRepo;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_success() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setSlotId(100L);
        appointment.setPatientId("PAT001");
        appointment.setStatus("booked");

        when(appointmentRepo.existsBySlotIdAndStatus(100L, "booked")).thenReturn(false);
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.writeValueAsString(any(Appointment.class))).thenReturn("{json}");

        Appointment result = bookingService.createBooking(100L, "PAT001");

        assertNotNull(result);
        assertEquals("PAT001", result.getPatientId());
        verify(outboxRepo, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testCreateBooking_slotAlreadyBooked() {
        when(appointmentRepo.existsBySlotIdAndStatus(200L, "booked")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(200L, "PAT002"));

        assertEquals("Slot already booked!", ex.getMessage());
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void testCancelBooking_success() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(2L);
        appointment.setSlotId(300L);
        appointment.setPatientId("PAT003");
        appointment.setStatus("booked");

        when(appointmentRepo.findById(2L)).thenReturn(Optional.of(appointment));
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.writeValueAsString(any(Appointment.class))).thenReturn("{json}");

        Appointment result = bookingService.cancelBooking(2L);

        assertEquals("cancelled", result.getStatus());
        verify(outboxRepo, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testCancelBooking_notFound() {
        when(appointmentRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking(999L));

        assertEquals("Appointment not found", ex.getMessage());
        verify(outboxRepo, never()).save(any());
    }
}
