package org.example.proccessworkload_microservice.controller;

import org.example.proccessworkload_microservice.dto.GetWorkload;
import org.example.proccessworkload_microservice.dto.WorkloadResponse;
import org.example.proccessworkload_microservice.exception.NoSuchTrainerException;
import org.example.proccessworkload_microservice.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WorkloadControllerTest {

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private WorkloadController workloadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWorkload_ShouldReturnWorkloadResponse_WhenServiceReturnsResponse() throws NoSuchTrainerException {
        GetWorkload getWorkload = new GetWorkload();
        getWorkload.setUsername("john.doe");
        getWorkload.setAction("ADD");
        getWorkload.setDuration(1.5);
        getWorkload.setTrainingDate(LocalDateTime.now());

        WorkloadResponse mockResponse = new WorkloadResponse();

        when(workloadService.processWorkLoad(getWorkload)).thenReturn(mockResponse);

        ResponseEntity<?> response = workloadController.getWorkload(getWorkload);

        assertEquals(ResponseEntity.ok(mockResponse), response);
        verify(workloadService, times(1)).processWorkLoad(getWorkload);
    }

    @Test
    void getWorkload_ShouldThrowException_WhenServiceThrowsNoSuchTrainerException() throws NoSuchTrainerException {
        GetWorkload getWorkload = new GetWorkload();
        getWorkload.setUsername("invalid.user");

        when(workloadService.processWorkLoad(getWorkload)).thenThrow(new NoSuchTrainerException("Trainer not found"));

        NoSuchTrainerException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        NoSuchTrainerException.class,
                        () -> workloadController.getWorkload(getWorkload)
                );

        assertEquals("Trainer not found", exception.getMessage());
        verify(workloadService, times(1)).processWorkLoad(getWorkload);
    }
}
