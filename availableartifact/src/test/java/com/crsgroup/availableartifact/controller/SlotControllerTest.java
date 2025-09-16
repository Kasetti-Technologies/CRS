package com.crsgroup.availableartifact.controller;

import com.crsgroup.availableartifact.model.Slot;
import com.crsgroup.availableartifact.service.SlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SlotControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SlotService slotService;  // Replaces @MockBean

    @InjectMocks
    private SlotController slotController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);  // Initialize @Mock and @InjectMocks
        mockMvc = MockMvcBuilders.standaloneSetup(slotController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Enables LocalDateTime support
    }

    @Test
    void testCreateSlot() throws Exception {
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setCentreId("DC900");
        slot.setServiceType("CT_SCAN");
        slot.setSlotTime(LocalDateTime.now());
        slot.setStatus("available");

        when(slotService.addSlot(any(Slot.class))).thenReturn(slot);

        mockMvc.perform(post("/slots/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.centreId").value("DC900"))
                .andExpect(jsonPath("$.serviceType").value("CT_SCAN"))
                .andExpect(jsonPath("$.status").value("available"));
    }

    @Test
    void testGetAvailability() throws Exception {
        Slot slot = new Slot();
        slot.setId(2L);
        slot.setCentreId("DC800");
        slot.setServiceType("MRI");
        slot.setSlotTime(LocalDateTime.now());
        slot.setStatus("available");

        when(slotService.getSlots("DC800", "MRI", "available"))
                .thenReturn(List.of(slot));

        mockMvc.perform(get("/slots/availability")
                        .param("centreId", "DC800")
                        .param("serviceType", "MRI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].centreId").value("DC800"))
                .andExpect(jsonPath("$[0].serviceType").value("MRI"))
                .andExpect(jsonPath("$[0].status").value("available"));
    }

    @Test
    void testUpdateSlot() throws Exception {
        Slot slot = new Slot();
        slot.setId(3L);
        slot.setCentreId("DC777");
        slot.setServiceType("XRAY");
        slot.setSlotTime(LocalDateTime.now());
        slot.setStatus("cancelled");

        when(slotService.updateSlot(any(Slot.class))).thenReturn(slot);

        mockMvc.perform(put("/slots/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"))
                .andExpect(jsonPath("$.centreId").value("DC777"))
                .andExpect(jsonPath("$.serviceType").value("XRAY"));
    }
}
