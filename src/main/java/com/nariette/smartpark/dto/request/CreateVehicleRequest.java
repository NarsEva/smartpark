package com.nariette.smartpark.dto.request;

import com.nariette.smartpark.entity.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateVehicleRequest {

    private String licensePlate;

    private VehicleType type;

    private String ownerName;
}