package com.example.pwctictactoebackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Player {

    private String username;
    private boolean isStarting;

}
