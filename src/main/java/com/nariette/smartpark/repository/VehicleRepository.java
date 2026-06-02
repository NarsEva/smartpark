package com.nariette.smartpark.repository;

import com.nariette.smartpark.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}