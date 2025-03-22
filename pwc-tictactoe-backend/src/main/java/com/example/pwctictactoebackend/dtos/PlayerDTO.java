package com.example.pwctictactoebackend.dtos;

import com.example.pwctictactoebackend.entities.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayerDTO {

    private String dtype = "Player";
    private String username;
    private boolean isStarting;

    public static PlayerDTO of(Player player) {
        return PlayerDTO.builder()
                .dtype("Player")
                .username(player.getUsername())
                .isStarting(player.isStarting())
                .build();
    }

}
