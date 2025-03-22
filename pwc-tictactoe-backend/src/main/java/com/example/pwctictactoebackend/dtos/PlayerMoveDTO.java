package com.example.pwctictactoebackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMoveDTO {

    private String dtype = "PlayerMove";
    private String roomName;
    private int x;
    private int y;
    private String playerName;

}
