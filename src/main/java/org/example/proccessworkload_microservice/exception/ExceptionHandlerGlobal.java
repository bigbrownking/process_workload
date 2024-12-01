package org.example.proccessworkload_microservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerGlobal {

    @ExceptionHandler(NoSuchTrainerException.class)
    public ResponseEntity<String> handleEntityNotFound(NoSuchTrainerException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
