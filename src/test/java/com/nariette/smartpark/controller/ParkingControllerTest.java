package com.nariette.smartpark.controller;

import com.nariette.smartpark.dto.request.CheckInRequest;
import com.nariette.smartpark.dto.request.CheckOutRequest;
import com.nariette.smartpark.dto.response.CheckInResponse;
import com.nariette.smartpark.dto.response.CheckOutResponse;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.VehicleType;
import com.nariette.smartpark.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingService parkingService;

    @Test
    void checkIn_shouldPassTimeZoneHeaderToService() throws Exception {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        CheckInResponse response = CheckInResponse.builder()
                .parkingRecordId(5L)
                .lotId("LOT-005")
                .licensePlate("HIJ-678")
                .checkInTime("Jun 4, 2026 12:47 AM +08:00")
                .build();

        when(parkingService.checkIn(eq("LOT-005"), eq(request), eq("Asia/Manila"))).thenReturn(response);

        mockMvc.perform(post("/api/parking-lots/{lotId}/check-in", "LOT-005")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Time-Zone", "Asia/Manila")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parkingRecordId").value(5))
                .andExpect(jsonPath("$.lotId").value("LOT-005"))
                .andExpect(jsonPath("$.licensePlate").value("HIJ-678"))
                .andExpect(jsonPath("$.checkInTime").value("Jun 4, 2026 12:47 AM +08:00"));

        verify(parkingService).checkIn(eq("LOT-005"), eq(request), eq("Asia/Manila"));
    }

    @Test
    void checkOut_shouldPassTimeZoneHeaderToService() throws Exception {
        CheckOutRequest request = CheckOutRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        CheckOutResponse response = CheckOutResponse.builder()
                .parkingRecordId(5L)
                .lotId("LOT-005")
                .licensePlate("HIJ-678")
                .checkInTime("Jun 4, 2026 12:47 AM +08:00")
                .checkOutTime("Jun 4, 2026 1:15 AM +08:00")
                .build();

        when(parkingService.checkOut(eq("LOT-005"), eq(request), eq("Asia/Manila"))).thenReturn(response);

        mockMvc.perform(post("/api/parking-lots/{lotId}/check-out", "LOT-005")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Time-Zone", "Asia/Manila")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingRecordId").value(5))
                .andExpect(jsonPath("$.lotId").value("LOT-005"))
                .andExpect(jsonPath("$.licensePlate").value("HIJ-678"))
                .andExpect(jsonPath("$.checkInTime").value("Jun 4, 2026 12:47 AM +08:00"))
                .andExpect(jsonPath("$.checkOutTime").value("Jun 4, 2026 1:15 AM +08:00"));

        verify(parkingService).checkOut(eq("LOT-005"), eq(request), eq("Asia/Manila"));
    }

    @Test
    void getParkedVehicles_shouldReturnVehicles() throws Exception {
        VehicleResponse vehicleResponse = VehicleResponse.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(parkingService.getParkedVehicles("LOT-005")).thenReturn(List.of(vehicleResponse));

        mockMvc.perform(get("/api/parking-lots/{lotId}/vehicles", "LOT-005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licensePlate").value("HIJ-678"))
                .andExpect(jsonPath("$[0].type").value("CAR"))
                .andExpect(jsonPath("$[0].ownerName").value("John Doe"));

        verify(parkingService).getParkedVehicles("LOT-005");
    }

    @Test
    void checkIn_shouldRejectInvalidLicensePlate() throws Exception {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ 678")
                .build();

        mockMvc.perform(post("/api/parking-lots/{lotId}/check-in", "LOT-005")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(
                        "licensePlate: License plate may contain only letters, numbers, and dashes"
                )));
    }
}
