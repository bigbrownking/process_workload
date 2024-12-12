package org.example.proccessworkload_microservice.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetWorkloadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("username")
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("training_date")
    private LocalDateTime trainingDate;

    @JsonProperty("duration")
    private double duration;

    @JsonProperty("action")
    private String action;
}
