package com.example.pwctictactoebackend.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<Map<String, String>> roomNotFoundException(RoomNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyRoomException.class)
    public ResponseEntity<Map<String, String>> roomIsEmpty(EmptyRoomException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<Map<String, String>> handlePlayerAlreadyInGameException(UsernameTakenException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

}
