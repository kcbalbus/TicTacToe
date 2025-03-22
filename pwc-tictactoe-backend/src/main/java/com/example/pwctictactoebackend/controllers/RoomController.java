package com.example.pwctictactoebackend.controllers;

import com.example.pwctictactoebackend.dtos.RoomDTO;
import com.example.pwctictactoebackend.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<RoomDTO> getRoom(@RequestParam String roomName) {
        return ResponseEntity.ok(roomService.getRoom(roomName));
    }

    @PostMapping("/findRoomForPlayer")
    public ResponseEntity<RoomDTO> chooseRoomForPlayer(@RequestParam String playerName) {
        return ResponseEntity.ok(roomService.findRoomForPlayer(playerName));
    }

}
