package org.example.service;

import org.example.dto.responces.WorkloadResponse;
import org.example.dto.requests.GetWorkloadRequest;
import org.example.exception.NoSuchTrainerException;

public interface WorkloadService {
    WorkloadResponse processWorkLoad(GetWorkloadRequest getWorkloadRequest) throws NoSuchTrainerException;
    double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException;
}
