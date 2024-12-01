package org.example.proccessworkload_microservice.service;

import org.example.proccessworkload_microservice.dto.GetWorkload;
import org.example.proccessworkload_microservice.dto.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;

public interface WorkloadService {
    WorkloadResponse processWorkLoad(GetWorkload getWorkload) throws NoSuchTrainerException;
    double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException;
}
