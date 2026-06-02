package com.nariette.smartpark.controller;

import com.nariette.smartpark.dto.request.CreateParkingLotRequest;
import com.nariette.smartpark.dto.response.ParkingLotResponse;
import com.nariette.smartpark.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingLotResponse createParkingLot(@RequestBody CreateParkingLotRequest request) {
        return parkingLotService.createParkingLot(request);
    }

    @GetMapping("/{lotId}/availability")
    public ParkingLotResponse getAvailability(@PathVariable String lotId) {
        return parkingLotService.getAvailability(lotId);
    }
}