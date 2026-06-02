package com.nariette.smartpark.mapper;

import com.nariette.smartpark.dto.request.CreateVehicleRequest;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(CreateVehicleRequest request) {
        return Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .type(request.getType())
                .ownerName(request.getOwnerName())
                .build();
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .licensePlate(vehicle.getLicensePlate())
                .type(vehicle.getType())
                .ownerName(vehicle.getOwnerName())
                .build();
    }
}