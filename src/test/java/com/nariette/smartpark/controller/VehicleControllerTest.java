package com.nariette.smartpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nariette.smartpark.dto.request.CreateVehicleRequest;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.VehicleType;
import com.nariette.smartpark.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @Test
    void createVehicle_shouldReturnCreatedResponse() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        when(vehicleService.createVehicle(eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("ABC-123"))
                .andExpect(jsonPath("$.type").value("CAR"))
                .andExpect(jsonPath("$.ownerName").value("Jane Doe"));

        verify(vehicleService).createVehicle(eq(request));
    }
}
