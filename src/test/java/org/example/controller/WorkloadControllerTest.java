package org.example.proccessworkload_microservice.controller;

import org.example.proccessworkload_microservice.dto.requests.GetWorkloadRequest;
import org.example.proccessworkload_microservice.dto.responces.WorkloadResponse;
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
        GetWorkloadRequest getWorkloadRequest = new GetWorkloadRequest();
        getWorkloadRequest.setUsername("john.doe");
        getWorkloadRequest.setAction("ADD");
        getWorkloadRequest.setDuration(1.5);
        getWorkloadRequest.setTrainingDate(LocalDateTime.now());

        WorkloadResponse mockResponse = new WorkloadResponse();

        when(workloadService.processWorkLoad(getWorkloadRequest)).thenReturn(mockResponse);

        ResponseEntity<?> response = workloadController.getWorkload(getWorkloadRequest);

        assertEquals(ResponseEntity.ok(mockResponse), response);
        verify(workloadService, times(1)).processWorkLoad(getWorkloadRequest);
    }

    @Test
    void getWorkload_ShouldThrowException_WhenServiceThrowsNoSuchTrainerException() throws NoSuchTrainerException {
        GetWorkloadRequest getWorkloadRequest = new GetWorkloadRequest();
        getWorkloadRequest.setUsername("invalid.user");

        when(workloadService.processWorkLoad(getWorkloadRequest)).thenThrow(new NoSuchTrainerException("Trainer not found"));

        NoSuchTrainerException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        NoSuchTrainerException.class,
                        () -> workloadController.getWorkload(getWorkloadRequest)
                );

        assertEquals("Trainer not found", exception.getMessage());
        verify(workloadService, times(1)).processWorkLoad(getWorkloadRequest);
    }
}
