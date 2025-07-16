package ru.yandex.practicum.telemetry.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sensors")
public class Sensor {
    @Id
    private String id;

    @Column(nullable = false)
    private String hubId;

}
