package com.example.pwctictactoebackend.services;

import com.example.pwctictactoebackend.dtos.BoardDTO;
import com.example.pwctictactoebackend.dtos.PlayerMoveDTO;
import com.example.pwctictactoebackend.entities.Player;
import com.example.pwctictactoebackend.entities.Room;
import com.example.pwctictactoebackend.messages.GameOverMessage;
import com.example.pwctictactoebackend.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void makeMove(PlayerMoveDTO playerMove) {

        Room room = roomRepository.getRoomByName(playerMove.getRoomName());

        makeMoveOnBoard(room, playerMove);

        BoardDTO board = new BoardDTO(room.getFields());

        if (room.getPlayer1().getUsername().equals(playerMove.getPlayerName())) {
            simpMessagingTemplate.convertAndSend("/queue/" + room.getPlayer2().getUsername(), board);
        } else {
            simpMessagingTemplate.convertAndSend("/queue/" + room.getPlayer1().getUsername(), board);
        }

        if (checkWin(room)) {
            startNewGame(room);
            return;
        }
        if (checkDraw(room)) {
            startNewGame(room);
            return;
        }
    }

    private void makeMoveOnBoard(Room room, PlayerMoveDTO playerMove) {
        List<List<Integer>> fields = room.getFields();
        int symbol = 1;
        if (room.getPlayer2().getUsername().equals(playerMove.getPlayerName())) {
            symbol = 2;
        }

        fields.get(playerMove.getX()).set(playerMove.getY(), symbol);
        room.setFields(fields);

        Player player1 = room.getPlayer1();
        player1.setStarting(!player1.isStarting());

        Player player2 = room.getPlayer2();
        player2.setStarting(!player2.isStarting());
    }

    private boolean checkWin(Room room) {
        if (checkRows(room)) {
            return true;
        }
        if (checkColumns(room)) {
            return true;
        }
        if (checkDiagonal(room)) {
            return true;
        }
        return false;
    }

    private void sendGameOverMessage(Room room, Integer winnerSymbol) {
        GameOverMessage gameOverMessageDTO = new GameOverMessage(winnerSymbol == 1, winnerSymbol == 0);
        simpMessagingTemplate.convertAndSend("/queue/" + room.getPlayer1().getUsername(), gameOverMessageDTO);
        gameOverMessageDTO.setWinner(winnerSymbol == 2);
        simpMessagingTemplate.convertAndSend("/queue/" + room.getPlayer2().getUsername(), gameOverMessageDTO);
    }

    private boolean checkRows(Room room) {
        for (List<Integer> row : room.getFields()) {
            if (row.stream().allMatch(cell -> cell.equals(row.get(0)) && cell != 0)) {
                sendGameOverMessage(room, row.get(0));
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns(Room room) {
        List<List<Integer>> board = room.getFields();
        for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
            final int col = columnIndex;
            if (IntStream.range(0, 3)
                    .allMatch(rowIndex -> board.get(rowIndex).get(col).equals(board.get(0).get(col)) && board.get(rowIndex).get(col) != 0)) {
                sendGameOverMessage(room, board.get(0).get(columnIndex));
                return true;
            }
        }
        return false;
    }


    private boolean checkDiagonal(Room room) {
        List<List<Integer>> board = room.getFields();

        // Pierwsza przekatna
        boolean diagonal1 = IntStream.range(0, 3).allMatch(i -> board.get(i).get(i).equals(board.get(0).get(0)) && board.get(i).get(i) != 0);
        if (diagonal1) {
            sendGameOverMessage(room, board.get(0).get(0));
            return true;
        }

        // Druga przekatna
        boolean diagonal2 = IntStream.range(0, 3).allMatch(i -> board.get(i).get(2 - i).equals(board.get(0).get(2)) && board.get(i).get(2 - i) != 0);
        if (diagonal2) {
            sendGameOverMessage(room, board.get(0).get(2));
            return true;
        }
        return false;
    }

    private boolean checkDraw(Room room) {
        int countEmptySquares = 0;
        List<List<Integer>> board = room.getFields();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board.get(i).get(j) == 0) {
                    countEmptySquares++;
                }
            }
        }
        if(countEmptySquares == 0) {
            sendGameOverMessage(room, 0);
            return true;
        }
        return false;
    }

    private void startNewGame(Room room) {
        room.initializeFields();
    }
}

