package org.example.proccessworkload_microservice.controller;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.example.proccessworkload_microservice.dto.GetWorkload;
import org.example.proccessworkload_microservice.dto.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;
import org.example.proccessworkload_microservice.service.WorkloadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class WorkloadController{

    @Lazy
    private final EurekaClient eurekaClient;

    private final WorkloadService workloadService;


    @PostMapping("/workload")
    public ResponseEntity<?> getWorkload(@RequestBody GetWorkload getWorkload) throws NoSuchTrainerException {
        WorkloadResponse workloadResponse = workloadService.processWorkLoad(getWorkload);
        return ResponseEntity.ok(workloadResponse);
    }
}
