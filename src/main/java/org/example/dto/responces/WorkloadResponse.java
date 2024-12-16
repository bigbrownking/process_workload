package org.example.dto.responces;

import lombok.Data;
import org.example.model.YearSummary;

import java.util.List;

@Data
public class WorkloadResponse {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private List<YearSummary> yearsList;
}
