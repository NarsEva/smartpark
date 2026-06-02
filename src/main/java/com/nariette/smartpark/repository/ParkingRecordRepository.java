package com.nariette.smartpark.repository;

import com.nariette.smartpark.entity.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {

    Optional<ParkingRecord> findByVehicleLicensePlateAndActiveTrue(String licensePlate);

    Optional<ParkingRecord> findByVehicleLicensePlateAndParkingLotLotIdAndActiveTrue(
            String licensePlate,
            String lotId
    );

    List<ParkingRecord> findByParkingLotLotIdAndActiveTrue(String lotId);
}