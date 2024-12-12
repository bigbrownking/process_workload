package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.responces.WorkloadResponse;
import org.example.model.MonthlyTraining;
import org.example.model.Trainer;
import org.example.dto.requests.GetWorkloadRequest;
import org.example.exception.NoSuchTrainerException;
import org.example.repository.TrainerRepository;
import org.example.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final TrainerRepository trainerRepository;
    private static final String WORKLOAD_QUEUE = "workload_queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    @Override
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

    @JmsListener(destination = WORKLOAD_QUEUE)
    public void processWorkloadMessage(GetWorkloadRequest getWorkloadRequest,
                                       @Header(name = "actionType", defaultValue = "UNKNOWN") String actionType,
                                       @Header(name = "username", defaultValue = "N/A") String username,
                                       @Header(name = "duration", defaultValue = "0") double duration) {
        LOGGER.debug("Received message: " + getWorkloadRequest);
        LOGGER.debug("Action Type: " + actionType);
        LOGGER.debug("Username: " + username);
        LOGGER.debug("Duration:  "+ duration);

        try {
            LOGGER.info("Received workload update message for trainer: {}", getWorkloadRequest);
            processWorkLoad(getWorkloadRequest);
        } catch (NoSuchTrainerException e) {
            LOGGER.error("Trainer not found for workload update: {}", getWorkloadRequest.getUsername());
        } catch (Exception e) {
            LOGGER.error("Failed to process workload message: {}", e.getMessage());
        }
    }
    @Override
    public double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException {
        Trainer trainer = getTrainerByUsername(username);
        String yearMonthKey = YearMonth.of(year, month).toString();
        return trainer.getTrainingHours()
                .getOrDefault(yearMonthKey, new MonthlyTraining())
                .getHours();
    }
}
