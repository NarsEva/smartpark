package com.nariette.smartpark.controller;

import com.nariette.smartpark.dto.request.CreateParkingLotRequest;
import com.nariette.smartpark.dto.response.ParkingLotResponse;
import com.nariette.smartpark.service.ParkingLotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingLotController.class)
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingLotService parkingLotService;

    @Test
    void createParkingLot_shouldReturnCreatedResponse() throws Exception {
        CreateParkingLotRequest request = CreateParkingLotRequest.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .build();

        ParkingLotResponse response = ParkingLotResponse.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(0)
                .availableSpaces(10)
                .build();

        when(parkingLotService.createParkingLot(eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotId").value("LOT-001"))
                .andExpect(jsonPath("$.location").value("Basement A"))
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.occupiedSpaces").value(0))
                .andExpect(jsonPath("$.availableSpaces").value(10));

        verify(parkingLotService).createParkingLot(eq(request));
    }

    @Test
    void getAvailability_shouldReturnResponse() throws Exception {
        ParkingLotResponse response = ParkingLotResponse.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(3)
                .availableSpaces(7)
                .build();

        when(parkingLotService.getAvailability("LOT-001")).thenReturn(response);

        mockMvc.perform(get("/api/parking-lots/{lotId}/availability", "LOT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotId").value("LOT-001"))
                .andExpect(jsonPath("$.occupiedSpaces").value(3))
                .andExpect(jsonPath("$.availableSpaces").value(7));

        verify(parkingLotService).getAvailability("LOT-001");
    }

    @Test
    void createParkingLot_shouldRejectInvalidLotId() throws Exception {
        CreateParkingLotRequest request = CreateParkingLotRequest.builder()
                .lotId("LOT-001-THIS-ID-IS-WAY-TOO-LONG-TO-PASS-THE-FIFTY-CHARACTER-LIMIT")
                .location("Basement A")
                .capacity(10)
                .build();

        mockMvc.perform(post("/api/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("lotId: Lot ID must not exceed 50 characters")));
    }
}
