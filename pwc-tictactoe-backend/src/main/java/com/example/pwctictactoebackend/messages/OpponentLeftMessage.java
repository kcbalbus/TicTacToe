package com.example.pwctictactoebackend.messages;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpponentLeftMessage {

    private String dtype = "OpponentLeftMessage";
    private String message = "Your opponent has left the game";

}
