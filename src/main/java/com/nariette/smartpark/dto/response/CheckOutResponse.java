package com.nariette.smartpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {

    private Long parkingRecordId;

    private String lotId;

    private String licensePlate;

    private String checkInTime;

    private String checkOutTime;
}
