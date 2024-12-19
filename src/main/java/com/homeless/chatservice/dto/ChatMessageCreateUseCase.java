package com.homeless.chatservice;

import com.homeless.chatservice.dto.ChatMessageCreateCommand;

public interface ChatMessageCreateUseCase {
    // 채팅 메세지 생성 매서드
    Long createChatMessage(ChatMessageCreateCommand command);
}
