package com.example.pwctictactoebackend.controllers;

import com.example.pwctictactoebackend.dtos.PlayerMoveDTO;
import com.example.pwctictactoebackend.entities.Room;
import com.example.pwctictactoebackend.services.GameService;
import com.example.pwctictactoebackend.services.RoomService;
import com.example.pwctictactoebackend.utils.SessionSubscriptionMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final GameService gameService;
    private final RoomService roomService;
    @Autowired
    private SessionSubscriptionMap subscriptionMap;

    @MessageMapping("/move")
    public void forwardMessage(PlayerMoveDTO moveOnBoardDTO) {
        gameService.makeMove(moveOnBoardDTO);
    }

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();

        subscriptionMap.addSubscription(sessionId, destination);
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();

        subscriptionMap.removeSubscription(sessionId, destination);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Set<String> subscribedTopics = subscriptionMap.getSubscriptions(sessionId);
        subscribedTopics.forEach(topic -> {
            int lastIndex = topic.lastIndexOf('/');
            String username = topic.substring(lastIndex + 1);
            Room room = roomService.findRoomOfPlayer(username);
            if (room != null) roomService.deletePlayerFromRoom(room.getRoomName(), username);
        });
        System.out.println("User disconnected: " + sessionId + " from topics: " + subscribedTopics);
    }
}
