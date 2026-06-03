package com.nariette.smartpark.repository;

import com.nariette.smartpark.entity.ParkingLot;
import com.nariette.smartpark.entity.ParkingRecord;
import com.nariette.smartpark.entity.Vehicle;
import com.nariette.smartpark.entity.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParkingRecordRepositoryTest {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @Test
    void shouldFindActiveParkingRecordByVehicleLicensePlate() {
        ParkingRecord parkingRecord = seedActiveParkingRecord("LOT-001", "ABC-123");

        assertThat(parkingRecordRepository.findByVehicleLicensePlateAndActiveTrue("ABC-123"))
                .contains(parkingRecord);
    }

    @Test
    void shouldFindActiveParkingRecordByVehicleAndLot() {
        ParkingRecord parkingRecord = seedActiveParkingRecord("LOT-001", "ABC-123");

        assertThat(parkingRecordRepository
                .findByVehicleLicensePlateAndParkingLotLotIdAndActiveTrue("ABC-123", "LOT-001"))
                .contains(parkingRecord);
    }

    @Test
    void shouldFindAllActiveParkingRecordsForLot() {
        ParkingRecord activeOne = seedActiveParkingRecord("LOT-001", "ABC-123");
        seedActiveParkingRecord("LOT-001", "XYZ-999");

        List<ParkingRecord> activeRecords = parkingRecordRepository.findByParkingLotLotIdAndActiveTrue("LOT-001");

        assertThat(activeRecords)
                .contains(activeOne)
                .hasSize(2);
    }

    @Test
    void parkingLotRepository_shouldPersistAndLoadParkingLot() {
        ParkingLot parkingLot = ParkingLot.builder()
                .lotId("LOT-010")
                .location("Annex")
                .capacity(20)
                .occupiedSpaces(4)
                .build();

        ParkingLot saved = parkingLotRepository.save(parkingLot);

        assertThat(parkingLotRepository.findById(saved.getLotId()))
                .contains(saved);
    }

    @Test
    void vehicleRepository_shouldPersistAndLoadVehicle() {
        Vehicle vehicle = Vehicle.builder()
                .licensePlate("LMN-456")
                .type(VehicleType.TRUCK)
                .ownerName("Mary Jane")
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);

        assertThat(vehicleRepository.findById(saved.getLicensePlate()))
                .contains(saved);
    }

    private ParkingRecord seedActiveParkingRecord(String lotId, String licensePlate) {
        ParkingLot parkingLot = parkingLotRepository.save(ParkingLot.builder()
                .lotId(lotId)
                .location("Basement")
                .capacity(10)
                .occupiedSpaces(1)
                .build());

        Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                .licensePlate(licensePlate)
                .type(VehicleType.CAR)
                .ownerName("John Doe")
                .build());

        return parkingRecordRepository.save(ParkingRecord.builder()
                .parkingLot(parkingLot)
                .vehicle(vehicle)
                .checkInTime(Instant.parse("2026-06-04T00:00:00Z"))
                .active(true)
                .build());
    }
}
