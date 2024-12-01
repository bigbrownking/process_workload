package org.example.proccessworkload_microservice.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class Trainer {

    @Id
    private String username;

    private String firstName;
    private String lastName;
    private boolean isActive;

    @ElementCollection
    @MapKeyColumn(name = "training_month")
    private Map<String, MonthlyTraining> trainingHours = new HashMap<>();
}
