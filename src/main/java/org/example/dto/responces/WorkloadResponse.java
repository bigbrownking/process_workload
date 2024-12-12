package org.example.dto.responces;

import lombok.Getter;
import lombok.Setter;
import org.example.model.MonthlyTraining;

import java.util.Map;

@Getter
@Setter
public class WorkloadResponse {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Map<String, MonthlyTraining> trainingSummary;
}
