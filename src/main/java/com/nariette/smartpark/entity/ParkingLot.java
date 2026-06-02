package com.nariette.smartpark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "parking_lots")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ParkingLot {
    @Id
    @Column(name = "lot_id", length = 50, nullable = false, unique = true)
    private String lotId;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "occupied_spaces", nullable = false)
    private Integer occupiedSpaces;
}
