package com.example.pwctictactoebackend.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException() {
        super("Room not found");
    }
}
