package org.example.model;

import jakarta.persistence.*;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "training_month")
    private Map<String, MonthlyTraining> trainingHours = new HashMap<>();
}
