package org.example.proccessworkload_microservice.controller;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.example.proccessworkload_microservice.dto.GetWorkloadRequest;
import org.example.proccessworkload_microservice.dto.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;
import org.example.proccessworkload_microservice.service.WorkloadService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WorkloadController{

    @Lazy
    private final EurekaClient eurekaClient;

    private final WorkloadService workloadService;


    @PostMapping("/workload")
    public ResponseEntity<?> getWorkload(@RequestBody GetWorkloadRequest getWorkloadRequest) throws NoSuchTrainerException {
        WorkloadResponse workloadResponse = workloadService.processWorkLoad(getWorkloadRequest);
        return ResponseEntity.ok(workloadResponse);
    }
}
