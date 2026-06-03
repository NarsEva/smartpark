package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CreateParkingLotRequest;
import com.nariette.smartpark.dto.response.ParkingLotResponse;
import com.nariette.smartpark.entity.ParkingLot;
import com.nariette.smartpark.exception.DuplicateResourceException;
import com.nariette.smartpark.exception.ResourceNotFoundException;
import com.nariette.smartpark.mapper.ParkingLotMapper;
import com.nariette.smartpark.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;

    public ParkingLotResponse createParkingLot(CreateParkingLotRequest request) {
        if (parkingLotRepository.existsById(request.getLotId())) {
            throw new DuplicateResourceException(
                    "Parking lot already exists with ID: " + request.getLotId()
            );
        }

        ParkingLot parkingLot = parkingLotMapper.toEntity(request);
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);

        return parkingLotMapper.toResponse(savedParkingLot);
    }

    public ParkingLotResponse getAvailability(String lotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parking lot not found with ID: " + lotId
                ));

        return parkingLotMapper.toResponse(parkingLot);
    }
}