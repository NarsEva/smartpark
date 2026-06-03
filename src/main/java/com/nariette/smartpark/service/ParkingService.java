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
import com.nariette.smartpark.mapper.VehicleMapper;
import com.nariette.smartpark.repository.ParkingLotRepository;
import com.nariette.smartpark.repository.ParkingRecordRepository;
import com.nariette.smartpark.repository.VehicleRepository;
import com.nariette.smartpark.dto.response.VehicleResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final VehicleMapper vehicleMapper;

    public CheckInResponse checkIn(String lotId, CheckInRequest request) {
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
                .checkInTime(LocalDateTime.now())
                .active(true)
                .build();

        parkingLot.setOccupiedSpaces(parkingLot.getOccupiedSpaces() + 1);

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        parkingLotRepository.save(parkingLot);

        return CheckInResponse.builder()
                .parkingRecordId(savedRecord.getId())
                .lotId(parkingLot.getLotId())
                .licensePlate(vehicle.getLicensePlate())
                .checkInTime(savedRecord.getCheckInTime())
                .build();
    }

    public CheckOutResponse checkOut(String lotId, CheckOutRequest request) {
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

        parkingRecord.setCheckOutTime(LocalDateTime.now());
        parkingRecord.setActive(false);

        parkingLot.setOccupiedSpaces(parkingLot.getOccupiedSpaces() - 1);

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        parkingLotRepository.save(parkingLot);

        return CheckOutResponse.builder()
                .parkingRecordId(savedRecord.getId())
                .lotId(parkingLot.getLotId())
                .licensePlate(savedRecord.getVehicle().getLicensePlate())
                .checkInTime(savedRecord.getCheckInTime())
                .checkOutTime(savedRecord.getCheckOutTime())
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
}