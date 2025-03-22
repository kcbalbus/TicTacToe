package com.example.pwctictactoebackend.utils.exceptions;

import lombok.Getter;

@Getter
public class EmptyRoomException extends RuntimeException {

    public EmptyRoomException() {
        super("Empty room");
    }
}

