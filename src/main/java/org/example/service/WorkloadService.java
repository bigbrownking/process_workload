package org.example.proccessworkload_microservice.service;

import org.example.proccessworkload_microservice.dto.requests.GetWorkloadRequest;
import org.example.proccessworkload_microservice.dto.responces.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;

public interface WorkloadService {
    WorkloadResponse processWorkLoad(GetWorkloadRequest getWorkloadRequest) throws NoSuchTrainerException;
    double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException;
}
