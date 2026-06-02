package com.nariette.smartpark.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateParkingLotRequest {

    private String lotId;

    private String location;

    private Integer capacity;
}