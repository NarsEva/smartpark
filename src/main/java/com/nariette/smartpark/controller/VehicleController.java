package com.nariette.smartpark.controller;

import com.nariette.smartpark.dto.request.CreateVehicleRequest;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse createVehicle(@RequestBody CreateVehicleRequest request) {
        return vehicleService.createVehicle(request);
    }
}