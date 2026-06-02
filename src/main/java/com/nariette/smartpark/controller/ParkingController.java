package com.nariette.smartpark.controller;

import com.nariette.smartpark.dto.request.CheckInRequest;
import com.nariette.smartpark.dto.request.CheckOutRequest;
import com.nariette.smartpark.dto.response.CheckInResponse;
import com.nariette.smartpark.dto.response.CheckOutResponse;
import com.nariette.smartpark.dto.response.VehicleResponse;
import com.nariette.smartpark.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/{lotId}/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(
            @PathVariable String lotId,
            @RequestBody CheckInRequest request
    ) {
        return parkingService.checkIn(lotId, request);
    }

    @PostMapping("/{lotId}/check-out")
    public CheckOutResponse checkOut(
            @PathVariable String lotId,
            @RequestBody CheckOutRequest request
    ) {
        return parkingService.checkOut(lotId, request);
    }

    @GetMapping("/{lotId}/vehicles")
    public List<VehicleResponse> getParkedVehicles(@PathVariable String lotId) {
        return parkingService.getParkedVehicles(lotId);
    }
}