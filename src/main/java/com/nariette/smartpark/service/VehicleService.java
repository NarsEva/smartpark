package com.nariette.smartpark.service;

import com.nariette.smartpark.dto.request.CreateVehicleRequest;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.entity.Vehicle;
import com.nariette.smartpark.mapper.VehicleMapper;
import com.nariette.smartpark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        if (vehicleRepository.existsById(request.getLicensePlate())) {
            throw new RuntimeException("Vehicle already exists with license plate: " + request.getLicensePlate());
        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.toResponse(savedVehicle);
    }
}