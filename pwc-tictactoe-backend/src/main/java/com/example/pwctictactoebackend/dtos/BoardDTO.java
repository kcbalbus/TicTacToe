package com.example.pwctictactoebackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BoardDTO {

    private String dtype = "Board";
    private List<List<Integer>> fields;

    public BoardDTO(List<List<Integer>> fields) {
        this.fields = fields;
    }

}
