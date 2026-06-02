package com.nariette.smartpark.mapper;

import com.nariette.smartpark.dto.request.CreateParkingLotRequest;
import com.nariette.smartpark.dto.response.ParkingLotResponse;
import com.nariette.smartpark.entity.ParkingLot;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapper {

    public ParkingLot toEntity(CreateParkingLotRequest request) {

        return ParkingLot.builder()
                .lotId(request.getLotId())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .occupiedSpaces(0)
                .build();
    }

    public ParkingLotResponse toResponse(ParkingLot parkingLot) {

        return ParkingLotResponse.builder()
                .lotId(parkingLot.getLotId())
                .location(parkingLot.getLocation())
                .capacity(parkingLot.getCapacity())
                .occupiedSpaces(parkingLot.getOccupiedSpaces())
                .availableSpaces(
                        parkingLot.getCapacity()
                                - parkingLot.getOccupiedSpaces())
                .build();
    }
}