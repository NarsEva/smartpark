package com.nariette.smartpark.dto.response;

import com.nariette.smartpark.entity.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {

    private String licensePlate;

    private VehicleType type;

    private String ownerName;
}