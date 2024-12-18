package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.responces.WorkloadResponse;
import org.example.dto.requests.GetWorkloadRequest;
import org.example.exception.NoSuchTrainerException;
import org.example.model.MonthSummary;
import org.example.model.Trainer;
import org.example.model.YearSummary;
import org.example.repository.TrainerRepository;
import org.example.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final TrainerRepository trainerRepository;
    private static final String WORKLOAD_QUEUE = "workload_queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    @Override
    public WorkloadResponse processWorkLoad(GetWorkloadRequest getWorkloadRequest) {
        String transactionId = UUID.randomUUID().toString();
        LOGGER.info("Transaction [{}]: Starting workload processing for username: {}", transactionId, getWorkloadRequest.getUsername());
        try {
            YearMonth yearMonth = YearMonth.from(getWorkloadRequest.getTrainingDate());

            Trainer trainer = trainerRepository.findByUsername(getWorkloadRequest.getUsername())
                    .orElseGet(() -> createNewTrainer(transactionId, getWorkloadRequest.getUsername(), yearMonth));

            YearSummary yearSummary = trainer.getYearsList()
                    .stream()
                    .filter(y -> y.getYear() == yearMonth.getYear())
                    .findFirst()
                    .orElseGet(() -> createNewYearSummary(transactionId, trainer, yearMonth.getYear()));

            MonthSummary monthSummary = yearSummary.getMonthsList()
                    .stream()
                    .filter(m -> m.getMonth().equalsIgnoreCase(yearMonth.getMonth().name()))
                    .findFirst()
                    .orElseGet(() -> createNewMonthSummary(transactionId, yearSummary, yearMonth.getMonth().name()));

            double durationInHours = Math.round((getWorkloadRequest.getDuration() / 60.0) * 100.0) / 100.0;
            updateMonthlyHours(transactionId, monthSummary, getWorkloadRequest.getAction(), durationInHours);

            trainerRepository.save(trainer);
            LOGGER.info("Transaction [{}]: Successfully processed workload for username: {}", transactionId, getWorkloadRequest.getUsername());
            return buildWorkloadResponse(trainer);
        } catch (Exception e) {
            LOGGER.error("Transaction [{}]: Failed to process workload. Error: {}", transactionId, e.getMessage());
            throw e;
        }
    }

    private Trainer createNewTrainer(String transactionId, String username, YearMonth yearMonth) {
        LOGGER.info("Transaction [{}]: Creating new trainer record for username: {}", transactionId, username);
        Trainer newTrainer = new Trainer();
        newTrainer.setUsername(username);
        newTrainer.setYearsList(new ArrayList<>());

        YearSummary yearSummary = createNewYearSummary(transactionId, newTrainer, yearMonth.getYear());
        createNewMonthSummary(transactionId, yearSummary, yearMonth.getMonth().name());

        return newTrainer;
    }

    private YearSummary createNewYearSummary(String transactionId, Trainer trainer, int year) {
        LOGGER.info("Transaction [{}]: Adding new year summary for year: {}", transactionId, year);
        YearSummary newYearSummary = new YearSummary();
        newYearSummary.setYear(year);
        newYearSummary.setMonthsList(new ArrayList<>());
        trainer.getYearsList().add(newYearSummary);
        return newYearSummary;
    }

    private MonthSummary createNewMonthSummary(String transactionId, YearSummary yearSummary, String month) {
        LOGGER.info("Transaction [{}]: Adding new month summary for month: {}", transactionId, month);
        MonthSummary newMonthSummary = new MonthSummary();
        newMonthSummary.setMonth(month);
        newMonthSummary.setTrainingsSummaryDuration(0.0);
        yearSummary.getMonthsList().add(newMonthSummary);
        return newMonthSummary;
    }

    private void updateMonthlyHours(String transactionId, MonthSummary monthSummary, String action, double duration) {
        double currentHours = monthSummary.getTrainingsSummaryDuration();
        if ("ADD".equalsIgnoreCase(action)) {
            LOGGER.info("Transaction [{}]: Adding {} hours to month: {}", transactionId, duration, monthSummary.getMonth());
            monthSummary.setTrainingsSummaryDuration(currentHours + duration);
        } else if ("DELETE".equalsIgnoreCase(action)) {
            LOGGER.info("Transaction [{}]: Subtracting {} hours from month: {}", transactionId, duration, monthSummary.getMonth());
            monthSummary.setTrainingsSummaryDuration(Math.max(0, currentHours - duration));
        }
    }

    private WorkloadResponse buildWorkloadResponse(Trainer trainer) {
        WorkloadResponse response = new WorkloadResponse();
        response.setUsername(trainer.getUsername());
        response.setFirstName(trainer.getFirstname());
        response.setLastName(trainer.getLastname());
        response.setYearsList(trainer.getYearsList());
        return response;
    }

    @JmsListener(destination = WORKLOAD_QUEUE)
    public void processWorkloadMessage(GetWorkloadRequest getWorkloadRequest,
                                       @Header(name = "actionType", defaultValue = "UNKNOWN") String actionType,
                                       @Header(name = "username", defaultValue = "N/A") String username,
                                       @Header(name = "duration", defaultValue = "0") double duration) {
        String transactionId = UUID.randomUUID().toString();
        LOGGER.info("Transaction [{}]: Received message for username: {}, action: {}, duration: {}", transactionId, username, actionType, duration);
        try {
            processWorkLoad(getWorkloadRequest);
        } catch (Exception e) {
            LOGGER.error("Transaction [{}]: Failed to process message. Error: {}", transactionId, e.getMessage());
        }
    }

    @Override
    public double getMonthlyTrainingHours(String username, int year, int month) throws NoSuchTrainerException {
        LOGGER.info("Fetching monthly training hours for username: {}, year: {}, month: {}", username, year, month);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchTrainerException("Trainer with username: " + username + " not found."));

        return trainer.getYearsList()
                .stream()
                .filter(y -> y.getYear() == year)
                .flatMap(y -> y.getMonthsList().stream())
                .filter(m -> m.getMonth().equalsIgnoreCase(YearMonth.of(year, month).getMonth().name()))
                .findFirst()
                .map(MonthSummary::getTrainingsSummaryDuration)
                .orElse(0.0);
    }
}
