package com.example.pwctictactoebackend.services;

import com.example.pwctictactoebackend.dtos.RoomDTO;
import com.example.pwctictactoebackend.entities.Player;
import com.example.pwctictactoebackend.entities.Room;
import com.example.pwctictactoebackend.messages.OpponentLeftMessage;
import com.example.pwctictactoebackend.repositories.RoomRepository;
import com.example.pwctictactoebackend.utils.exceptions.UsernameTakenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;


    private boolean isPlayerAlreadyInGame(String playerName) {
        return roomRepository.getRooms().stream()
                .anyMatch(room -> room.getPlayer1() != null && room.getPlayer1().getUsername().equals(playerName) ||
                        room.getPlayer2() != null && room.getPlayer2().getUsername().equals(playerName));
    }

    public Room findRoomOfPlayer(String playerName) {
        return roomRepository.getRooms().stream()
                .filter(room -> (room.getPlayer1() != null && room.getPlayer1().getUsername().equals(playerName)) ||
                        (room.getPlayer2() != null && room.getPlayer2().getUsername().equals(playerName)))
                .findFirst()
                .orElse(null);
    }


    public RoomDTO findRoomForPlayer(String playerName) {
        if (isPlayerAlreadyInGame(playerName)) {
            throw new UsernameTakenException("Username " + playerName + " is already taken!");
        }

        Room room;
        Optional<Room> optionalRoom = roomRepository.getRoomWithOneFreeSlot();
        Player player = new Player(playerName, false);
        if(optionalRoom.isPresent()) {
            room = optionalRoom.get();
            room.setPlayer2(player);
            room.setFreeSlots(0);
            chooseStartingPlayer(room);
            sendStartGameMessage(room);
            return RoomDTO.of(room);
        }

        Optional<Room> optionalRoom2 = roomRepository.getRoomWithTwoFreeSlots();
        if(optionalRoom2.isPresent()) {
            room = optionalRoom2.get();
            room.setPlayer1(player);
            room.setFreeSlots(1);
            return RoomDTO.of(room);
        }

        Room newRoom = new Room("room" + roomRepository.getRoomCounter());
        roomRepository.setRoomCounter(roomRepository.getRoomCounter() + 1);
        newRoom.setPlayer1(player);
        newRoom.setFreeSlots(1);
        roomRepository.addRoom(newRoom);
        return RoomDTO.of(newRoom);
    }

    private void chooseStartingPlayer(Room room) {
        boolean isStarting = new Random().nextBoolean();
        room.getPlayer1().setStarting(isStarting);
        room.getPlayer2().setStarting(!isStarting);
    }

    private void sendStartGameMessage(Room room) {
        sendStartGameMessageToPlayer(room.getPlayer1().getUsername(), room);
        sendStartGameMessageToPlayer(room.getPlayer2().getUsername(), room);
    }

    private void sendStartGameMessageToPlayer(String playerName, Room room) {
        simpMessagingTemplate.convertAndSend("/queue/" + playerName, RoomDTO.of(room));
    }

    public void deletePlayerFromRoom(String roomName, String playerName) {

        Room room = roomRepository.getRoomByName(roomName);
        if(room.getFreeSlots() == 1) {
            roomRepository.removeRoom(room);
        } else {
            if(room.getPlayer1().getUsername().equals(playerName)) {
                room.setPlayer1(room.getPlayer2());
            }
            room.setPlayer2(null);
            room.setFreeSlots(1);
            room.initializeFields();
            simpMessagingTemplate.convertAndSend("/queue/" + room.getPlayer1().getUsername(), new OpponentLeftMessage());
        }
    }

    public RoomDTO getRoom(String roomName) {
        return RoomDTO.of(roomRepository.getRoomByName(roomName));
    }
}
