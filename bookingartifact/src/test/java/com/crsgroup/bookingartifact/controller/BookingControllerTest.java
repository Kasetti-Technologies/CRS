package com.crsgroup.bookingartifact.controller;

import com.crsgroup.bookingartifact.model.Appointment;
import com.crsgroup.bookingartifact.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;
    @SuppressWarnings("unused")
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateBooking() throws Exception {
        Appointment appt = new Appointment();
        appt.setId(1L);
        appt.setSlotId(10L);
        appt.setPatientId("PAT100");
        appt.setStatus("booked");

        when(bookingService.createBooking(10L, "PAT100")).thenReturn(appt);

        mockMvc.perform(post("/appointments")
                        .param("slotId", "10")
                        .param("patientId", "PAT100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(10))
                .andExpect(jsonPath("$.patientId").value("PAT100"))
                .andExpect(jsonPath("$.status").value("booked"));
    }

    @Test
    void testCancelBooking() throws Exception {
        Appointment appt = new Appointment();
        appt.setId(2L);
        appt.setSlotId(20L);
        appt.setPatientId("PAT200");
        appt.setStatus("cancelled");

        when(bookingService.cancelBooking(2L)).thenReturn(appt);

        mockMvc.perform(post("/appointments/2/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("cancelled"))
                .andExpect(jsonPath("$.patientId").value("PAT200"));
    }
}
