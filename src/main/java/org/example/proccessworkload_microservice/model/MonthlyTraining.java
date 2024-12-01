package org.example.proccessworkload_microservice.model;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class MonthlyTraining {
    private Double hours = 0.0;
}
