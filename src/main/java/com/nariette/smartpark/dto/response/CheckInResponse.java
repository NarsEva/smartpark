package com.nariette.smartpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {

    private Long parkingRecordId;

    private String lotId;

    private String licensePlate;

    private LocalDateTime checkInTime;
}

