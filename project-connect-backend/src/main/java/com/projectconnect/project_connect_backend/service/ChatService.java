package com.projectconnect.project_connect_backend.service;

import com.projectconnect.project_connect_backend.dto.ChatMessage;
import com.projectconnect.project_connect_backend.entity.ChatMessageEntity;
import com.projectconnect.project_connect_backend.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessage chatMessage) {

        ChatMessageEntity entity = new ChatMessageEntity(
            chatMessage.getSenderId(),
            chatMessage.getReceiverId(),
            chatMessage.getMessage()
        );
        chatMessageRepository.save(entity);


        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiverId().toString(),
            "/queue/messages",
            chatMessage
        );
    }

    public List<ChatMessage> getChatHistory(Long userId1, Long userId2) {
        List<ChatMessageEntity> entities = chatMessageRepository.findChatHistory(userId1, userId2);
        return entities.stream()
                .map(entity -> {
                    ChatMessage message = new ChatMessage();
                    message.setSenderId(entity.getSenderId());
                    message.setReceiverId(entity.getReceiverId());
                    message.setMessage(entity.getMessage());
                    message.setTimestamp(entity.getTimestamp());
                    return message;
                })
                .collect(Collectors.toList());
    }
}
