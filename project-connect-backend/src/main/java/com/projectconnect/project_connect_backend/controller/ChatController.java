package com.projectconnect.project_connect_backend.controller;

import com.projectconnect.project_connect_backend.dto.ChatMessage;
import com.projectconnect.project_connect_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatService.sendMessage(chatMessage);
    }

    @GetMapping("/api/chat/history/{userId1}/{userId2}")
    public List<ChatMessage> getChatHistory(@PathVariable Long userId1, @PathVariable Long userId2) {
        return chatService.getChatHistory(userId1, userId2);
    }
}
