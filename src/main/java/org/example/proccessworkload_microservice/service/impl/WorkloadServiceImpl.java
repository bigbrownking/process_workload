package org.example.proccessworkload_microservice.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.proccessworkload_microservice.dto.GetWorkloadRequest;
import org.example.proccessworkload_microservice.dto.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;
import org.example.proccessworkload_microservice.model.MonthlyTraining;
import org.example.proccessworkload_microservice.model.Trainer;
import org.example.proccessworkload_microservice.repository.TrainerRepository;
import org.example.proccessworkload_microservice.service.WorkloadService;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final TrainerRepository trainerRepository;

    @Override
    @CircuitBreaker(name = "workloadService", fallbackMethod = "processWorkLoadFallback")
    public WorkloadResponse processWorkLoad(GetWorkloadRequest getWorkloadRequest) throws NoSuchTrainerException {
        Trainer trainer = getTrainerByUsername(getWorkloadRequest.getUsername());
        YearMonth yearMonth = YearMonth.from(getWorkloadRequest.getTrainingDate());
        String yearMonthKey = yearMonth.toString();

        MonthlyTraining monthlyTraining = trainer.getTrainingHours()
                .computeIfAbsent(yearMonthKey, k -> new MonthlyTraining());

        double durationInHours = Math.round((getWorkloadRequest.getDuration() / 60.0) * 100.0) / 100.0;
        updateMonthlyHours(monthlyTraining, getWorkloadRequest.getAction(), durationInHours);
        trainer.getTrainingHours().put(yearMonthKey, monthlyTraining);
        trainerRepository.save(trainer);

        return buildWorkloadResponse(trainer);
    }
    private Trainer getTrainerByUsername(String username) throws NoSuchTrainerException {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchTrainerException("Trainer with username: " + username + " not found."));
    }

    private void updateMonthlyHours(MonthlyTraining monthlyTraining, String action, double duration) {
        double currentHours = monthlyTraining.getHours();
        if ("ADD".equalsIgnoreCase(action)) {
            monthlyTraining.setHours(currentHours + duration);
        } else if ("DELETE".equalsIgnoreCase(action)) {
            monthlyTraining.setHours(Math.max(0, currentHours - duration));
        }
    }

    private WorkloadResponse buildWorkloadResponse(Trainer trainer) {
        WorkloadResponse workloadResponse = new WorkloadResponse();
        workloadResponse.setUsername(trainer.getUsername());
        workloadResponse.setFirstName(trainer.getFirstName());
        workloadResponse.setLastName(trainer.getLastName());
        workloadResponse.setActive(trainer.isActive());
        workloadResponse.setTrainingSummary(trainer.getTrainingHours());
        return workloadResponse;
    }

    @Override
    public double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException {
        Trainer trainer = getTrainerByUsername(username);
        String yearMonthKey = YearMonth.of(year, month).toString();
        return trainer.getTrainingHours()
                .getOrDefault(yearMonthKey, new MonthlyTraining())
                .getHours();
    }

    private WorkloadResponse processWorkLoadFallback(GetWorkloadRequest getWorkloadRequest, Throwable throwable) {
        WorkloadResponse response = new WorkloadResponse();
        response.setUsername(getWorkloadRequest.getUsername());
        response.setFirstName("N/A");
        response.setLastName("N/A");
        response.setActive(false);
        response.setTrainingSummary(Collections.emptyMap());
        return response;
    }
}
