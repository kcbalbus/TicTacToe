package com.example.pwctictactoebackend.messages;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameOverMessage {

    private String dtype = "GameOverMessage";
    private boolean isWinner;
    private boolean isDraw;

    public GameOverMessage(boolean isWinner, boolean isDraw) {
        this.isWinner = isWinner;
        this.isDraw = isDraw;
    }

}
