package com.nariette.smartpark.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingLotResponse {

    private String lotId;

    private String location;

    private Integer capacity;

    private Integer occupiedSpaces;

    private Integer availableSpaces;
}