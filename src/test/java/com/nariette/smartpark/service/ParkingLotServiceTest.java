package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CreateParkingLotRequest;
import com.nariette.smartpark.dto.response.ParkingLotResponse;
import com.nariette.smartpark.entity.ParkingLot;
import com.nariette.smartpark.exception.DuplicateResourceException;
import com.nariette.smartpark.exception.ResourceNotFoundException;
import com.nariette.smartpark.mapper.ParkingLotMapper;
import com.nariette.smartpark.repository.ParkingLotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private ParkingLotMapper parkingLotMapper;

    @InjectMocks
    private ParkingLotService parkingLotService;

    @Test
    void createParkingLot_shouldCreateAndReturnResponse() {
        CreateParkingLotRequest request = CreateParkingLotRequest.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .build();

        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(0)
                .build();

        ParkingLotResponse expectedResponse = ParkingLotResponse.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(0)
                .availableSpaces(10)
                .build();

        when(parkingLotRepository.existsById("LOT-001")).thenReturn(false);
        when(parkingLotMapper.toEntity(request)).thenReturn(parkingLot);
        when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(parkingLot);
        when(parkingLotMapper.toResponse(parkingLot)).thenReturn(expectedResponse);

        ParkingLotResponse response = parkingLotService.createParkingLot(request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void createParkingLot_shouldThrowWhenLotAlreadyExists() {
        CreateParkingLotRequest request = CreateParkingLotRequest.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .build();

        when(parkingLotRepository.existsById("LOT-001")).thenReturn(true);

        assertThatThrownBy(() -> parkingLotService.createParkingLot(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Parking lot already exists with ID: LOT-001");
    }

    @Test
    void getAvailability_shouldReturnParkingLotResponse() {
        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(3)
                .build();

        ParkingLotResponse expectedResponse = ParkingLotResponse.builder()
                .lotId("LOT-001")
                .location("Basement A")
                .capacity(10)
                .occupiedSpaces(3)
                .availableSpaces(7)
                .build();

        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(parkingLot));
        when(parkingLotMapper.toResponse(parkingLot)).thenReturn(expectedResponse);

        ParkingLotResponse response = parkingLotService.getAvailability("LOT-001");

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void getAvailability_shouldThrowWhenLotNotFound() {
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingLotService.getAvailability("LOT-001"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Parking lot not found with ID: LOT-001");
    }
}
