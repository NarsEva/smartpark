package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CheckInRequest;
import com.nariette.smartpark.dto.request.CheckOutRequest;
import com.nariette.smartpark.dto.response.CheckInResponse;
import com.nariette.smartpark.dto.response.CheckOutResponse;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.ParkingLot;
import com.nariette.smartpark.entity.ParkingRecord;
import com.nariette.smartpark.entity.Vehicle;
import com.nariette.smartpark.entity.VehicleType;
import com.nariette.smartpark.exception.BusinessRuleException;
import com.nariette.smartpark.exception.ResourceNotFoundException;
import com.nariette.smartpark.mapper.VehicleMapper;
import com.nariette.smartpark.repository.ParkingLotRepository;
import com.nariette.smartpark.repository.ParkingRecordRepository;
import com.nariette.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private static final ZoneId MANILA = ZoneId.of("Asia/Manila");
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a XXX");

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingRecordRepository parkingRecordRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private ParkingService parkingService;

    @Test
    void checkIn_shouldCreateRecordAndFormatTime() {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-005")
                .location("Ground Floor")
                .capacity(10)
                .occupiedSpaces(0)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(parkingLotRepository.findById("LOT-005")).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findById("HIJ-678")).thenReturn(Optional.of(vehicle));
        when(parkingRecordRepository.findByVehicleLicensePlateAndActiveTrue("HIJ-678")).thenReturn(Optional.empty());
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenAnswer(invocation -> {
            ParkingRecord record = invocation.getArgument(0);
            record.setId(5L);
            return record;
        });
        when(parkingLotRepository.save(any(ParkingLot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<ParkingRecord> recordCaptor = ArgumentCaptor.forClass(ParkingRecord.class);
        ArgumentCaptor<ParkingLot> lotCaptor = ArgumentCaptor.forClass(ParkingLot.class);

        CheckInResponse response = parkingService.checkIn("LOT-005", request, MANILA.getId());

        verify(parkingRecordRepository).save(recordCaptor.capture());
        verify(parkingLotRepository).save(lotCaptor.capture());

        ParkingRecord savedRecord = recordCaptor.getValue();

        assertThat(response.getParkingRecordId()).isEqualTo(5L);
        assertThat(response.getLotId()).isEqualTo("LOT-005");
        assertThat(response.getLicensePlate()).isEqualTo("HIJ-678");
        assertThat(response.getCheckInTime()).isEqualTo(format(savedRecord.getCheckInTime(), MANILA));
        assertThat(lotCaptor.getValue().getOccupiedSpaces()).isEqualTo(1);
    }

    @Test
    void checkIn_shouldThrowWhenVehicleAlreadyHasActiveRecord() {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-005")
                .location("Ground Floor")
                .capacity(10)
                .occupiedSpaces(0)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(parkingLotRepository.findById("LOT-005")).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findById("HIJ-678")).thenReturn(Optional.of(vehicle));
        when(parkingRecordRepository.findByVehicleLicensePlateAndActiveTrue("HIJ-678"))
                .thenReturn(Optional.of(new ParkingRecord()));

        assertThatThrownBy(() -> parkingService.checkIn("LOT-005", request, MANILA.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Vehicle already has an active parking record: HIJ-678");

        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
        verify(parkingLotRepository, never()).save(any(ParkingLot.class));
    }

    @Test
    void checkIn_shouldThrowWhenParkingLotIsFull() {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-005")
                .location("Ground Floor")
                .capacity(1)
                .occupiedSpaces(1)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(parkingLotRepository.findById("LOT-005")).thenReturn(Optional.of(parkingLot));
        when(vehicleRepository.findById("HIJ-678")).thenReturn(Optional.of(vehicle));
        when(parkingRecordRepository.findByVehicleLicensePlateAndActiveTrue("HIJ-678"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingService.checkIn("LOT-005", request, MANILA.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Parking lot is full: LOT-005");

        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
        verify(parkingLotRepository, never()).save(any(ParkingLot.class));
    }

    @Test
    void checkIn_shouldThrowWhenTimeZoneIsInvalid() {
        CheckInRequest request = CheckInRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        assertThatThrownBy(() -> parkingService.checkIn("LOT-005", request, "Invalid/Zone"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid time zone: Invalid/Zone");
    }

    @Test
    void checkOut_shouldCloseRecordAndFormatTimes() {
        CheckOutRequest request = CheckOutRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-005")
                .location("Ground Floor")
                .capacity(10)
                .occupiedSpaces(1)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        ParkingRecord parkingRecord = ParkingRecord.builder()
                .id(5L)
                .parkingLot(parkingLot)
                .vehicle(vehicle)
                .checkInTime(Instant.parse("2026-06-03T16:00:00Z"))
                .active(true)
                .build();

        when(parkingRecordRepository.findByVehicleLicensePlateAndParkingLotLotIdAndActiveTrue("HIJ-678", "LOT-005"))
                .thenReturn(Optional.of(parkingRecord));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(parkingLotRepository.save(any(ParkingLot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<ParkingRecord> recordCaptor = ArgumentCaptor.forClass(ParkingRecord.class);
        ArgumentCaptor<ParkingLot> lotCaptor = ArgumentCaptor.forClass(ParkingLot.class);

        CheckOutResponse response = parkingService.checkOut("LOT-005", request, MANILA.getId());

        verify(parkingRecordRepository).save(recordCaptor.capture());
        verify(parkingLotRepository).save(lotCaptor.capture());

        ParkingRecord savedRecord = recordCaptor.getValue();

        assertThat(response.getParkingRecordId()).isEqualTo(5L);
        assertThat(response.getLotId()).isEqualTo("LOT-005");
        assertThat(response.getLicensePlate()).isEqualTo("HIJ-678");
        assertThat(response.getCheckInTime()).isEqualTo(format(Instant.parse("2026-06-03T16:00:00Z"), MANILA));
        assertThat(response.getCheckOutTime()).isEqualTo(format(savedRecord.getCheckOutTime(), MANILA));
        assertThat(savedRecord.getActive()).isFalse();
        assertThat(lotCaptor.getValue().getOccupiedSpaces()).isEqualTo(0);
    }

    @Test
    void checkOut_shouldThrowWhenActiveRecordDoesNotExist() {
        CheckOutRequest request = CheckOutRequest.builder()
                .licensePlate("HIJ-678")
                .build();

        when(parkingRecordRepository.findByVehicleLicensePlateAndParkingLotLotIdAndActiveTrue("HIJ-678", "LOT-005"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingService.checkOut("LOT-005", request, MANILA.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Active parking record not found for vehicle HIJ-678 in lot LOT-005");
    }

    @Test
    void getParkedVehicles_shouldReturnMappedVehicles() {
        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-005")
                .location("Ground Floor")
                .capacity(10)
                .occupiedSpaces(1)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        ParkingRecord parkingRecord = ParkingRecord.builder()
                .id(5L)
                .parkingLot(parkingLot)
                .vehicle(vehicle)
                .checkInTime(Instant.parse("2026-06-03T16:00:00Z"))
                .active(true)
                .build();

        VehicleResponse expectedResponse = VehicleResponse.builder()
                .licensePlate("HIJ-678")
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build();

        when(parkingLotRepository.findById("LOT-005")).thenReturn(Optional.of(parkingLot));
        when(parkingRecordRepository.findByParkingLotLotIdAndActiveTrue("LOT-005"))
                .thenReturn(List.of(parkingRecord));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(expectedResponse);

        List<VehicleResponse> response = parkingService.getParkedVehicles("LOT-005");

        assertThat(response).containsExactly(expectedResponse);
    }

    private String format(Instant instant, ZoneId zoneId) {
        return DISPLAY_TIME_FORMATTER.format(ZonedDateTime.ofInstant(instant, zoneId));
    }
}
