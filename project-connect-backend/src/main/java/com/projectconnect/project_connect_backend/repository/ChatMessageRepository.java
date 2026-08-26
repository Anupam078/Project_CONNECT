package com.projectconnect.project_connect_backend.repository;


import com.projectconnect.project_connect_backend.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    @Query("SELECT c FROM ChatMessageEntity c WHERE " +
           "(c.senderId = ?1 AND c.receiverId = ?2) OR " +
           "(c.senderId = ?2 AND c.receiverId = ?1) " +
           "ORDER BY c.timestamp")
    List<ChatMessageEntity> findChatHistory(Long userId1, Long userId2);
}
