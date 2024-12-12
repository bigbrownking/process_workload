package org.example.dto.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetWorkloadRequest {

    private String username;

    private String firstName;

    private String lastName;

    private boolean isActive;

    private LocalDateTime trainingDate;

    private double duration;

    private String action;

    @JsonCreator
    public GetWorkloadRequest(@JsonProperty("username") String username,
                              @JsonProperty("first_name") String firstName,
                              @JsonProperty("last_name") String lastName,
                              @JsonProperty("is_active") boolean isActive,
                              @JsonProperty("training_date") LocalDateTime trainingDate,
                              @JsonProperty("duration") double duration,
                              @JsonProperty("action") String action) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.duration = duration;
        this.action = action;
    }
}
