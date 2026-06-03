package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CreateVehicleRequest;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.Vehicle;
import com.nariette.smartpark.entity.VehicleType;
import com.nariette.smartpark.exception.DuplicateResourceException;
import com.nariette.smartpark.mapper.VehicleMapper;
import com.nariette.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void createVehicle_shouldCreateAndReturnResponse() {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        VehicleResponse expectedResponse = VehicleResponse.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        when(vehicleRepository.existsById("ABC-123")).thenReturn(false);
        when(vehicleMapper.toEntity(request)).thenReturn(vehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
        when(vehicleMapper.toResponse(vehicle)).thenReturn(expectedResponse);

        VehicleResponse response = vehicleService.createVehicle(request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void createVehicle_shouldThrowWhenVehicleAlreadyExists() {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .licensePlate("ABC-123")
                .type(VehicleType.CAR)
                .ownerName("Jane Doe")
                .build();

        when(vehicleRepository.existsById("ABC-123")).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Vehicle already exists with license plate: ABC-123");
    }
}
