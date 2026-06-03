package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CheckInRequest;
import com.nariette.smartpark.dto.request.CheckOutRequest;
import com.nariette.smartpark.dto.response.CheckInResponse;
import com.nariette.smartpark.dto.response.CheckOutResponse;
import com.nariette.smartpark.entity.ParkingLot;
import com.nariette.smartpark.entity.ParkingRecord;
import com.nariette.smartpark.entity.Vehicle;
import com.nariette.smartpark.exception.BusinessRuleException;
import com.nariette.smartpark.exception.ResourceNotFoundException;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.mapper.VehicleMapper;
import com.nariette.smartpark.repository.ParkingLotRepository;
import com.nariette.smartpark.repository.ParkingRecordRepository;
import com.nariette.smartpark.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {

    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a XXX");

    private final ParkingLotRepository parkingLotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final VehicleMapper vehicleMapper;

    public CheckInResponse checkIn(String lotId, CheckInRequest request, String timeZone) {
        ZoneId zoneId = resolveZoneId(timeZone);

        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parking lot not found with ID: " + lotId)
                );

        Vehicle vehicle = vehicleRepository.findById(request.getLicensePlate())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with license plate: " + request.getLicensePlate()
                        )
                );

        if (parkingRecordRepository.findByVehicleLicensePlateAndActiveTrue(request.getLicensePlate()).isPresent()) {
            throw new BusinessRuleException(
                    "Vehicle already has an active parking record: " + request.getLicensePlate()
            );
        }

        if (parkingLot.getOccupiedSpaces() >= parkingLot.getCapacity()) {
            throw new BusinessRuleException("Parking lot is full: " + lotId);
        }

        ParkingRecord parkingRecord = ParkingRecord.builder()
                .parkingLot(parkingLot)
                .vehicle(vehicle)
                .checkInTime(Instant.now())
                .active(true)
                .build();

        parkingLot.setOccupiedSpaces(parkingLot.getOccupiedSpaces() + 1);

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        parkingLotRepository.save(parkingLot);

        return CheckInResponse.builder()
                .parkingRecordId(savedRecord.getId())
                .lotId(parkingLot.getLotId())
                .licensePlate(vehicle.getLicensePlate())
                .checkInTime(formatInstant(savedRecord.getCheckInTime(), zoneId))
                .build();
    }

    public CheckOutResponse checkOut(String lotId, CheckOutRequest request, String timeZone) {
        ZoneId zoneId = resolveZoneId(timeZone);

        ParkingRecord parkingRecord = parkingRecordRepository
                .findByVehicleLicensePlateAndParkingLotLotIdAndActiveTrue(
                        request.getLicensePlate(),
                        lotId
                )
                .orElseThrow(() -> new BusinessRuleException(
                        "Active parking record not found for vehicle "
                                + request.getLicensePlate()
                                + " in lot "
                                + lotId
                ));

        ParkingLot parkingLot = parkingRecord.getParkingLot();

        parkingRecord.setCheckOutTime(Instant.now());
        parkingRecord.setActive(false);

        parkingLot.setOccupiedSpaces(parkingLot.getOccupiedSpaces() - 1);

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        parkingLotRepository.save(parkingLot);

        return CheckOutResponse.builder()
                .parkingRecordId(savedRecord.getId())
                .lotId(parkingLot.getLotId())
                .licensePlate(savedRecord.getVehicle().getLicensePlate())
                .checkInTime(formatInstant(savedRecord.getCheckInTime(), zoneId))
                .checkOutTime(formatInstant(savedRecord.getCheckOutTime(), zoneId))
                .build();
    }

    public List<VehicleResponse> getParkedVehicles(String lotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parking lot not found with ID: " + lotId
                        )
                );

        return parkingRecordRepository.findByParkingLotLotIdAndActiveTrue(parkingLot.getLotId())
                .stream()
                .map(parkingRecord -> vehicleMapper.toResponse(parkingRecord.getVehicle()))
                .toList();
    }

    private ZoneId resolveZoneId(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return ZoneId.systemDefault();
        }

        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException ex) {
            throw new BusinessRuleException("Invalid time zone: " + timeZone);
        }
    }

    private String formatInstant(Instant instant, ZoneId zoneId) {
        return instant == null
                ? null
                : DISPLAY_TIME_FORMATTER.format(ZonedDateTime.ofInstant(instant, zoneId));
    }
}
