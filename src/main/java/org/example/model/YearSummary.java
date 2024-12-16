package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class YearSummary {
    private Integer year;
    private List<MonthSummary> monthsList;
}
