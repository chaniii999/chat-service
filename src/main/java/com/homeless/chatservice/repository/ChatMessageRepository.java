package com.homeless.chatservice.repository;



import com.homeless.chatservice.common.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomId(Long roomId);  // 방 번호로 메시지 찾기
}