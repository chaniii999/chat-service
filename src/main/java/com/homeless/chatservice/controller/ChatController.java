package com.homeless.chatservice.controller;

import com.homeless.chatservice.dto.ChatMessageCreateCommand;
import com.homeless.chatservice.dto.ChatMessageRequest;
import com.homeless.chatservice.dto.ChatMessageResponse;
import com.homeless.chatservice.service.ChatMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;

    // WebSocket 메시지 전송 처리
    @MessageMapping("/rooms/{roomId}/send")
    @SendTo("/topic/public/rooms/{roomId}")
    public ChatMessageResponse sendMessageWebSocket(@DestinationVariable Long roomId, @Payload ChatMessageRequest chatMessage) {
        try {
            // 채팅 메시지 생성
            ChatMessageCreateCommand chatMessageCreateCommand = new ChatMessageCreateCommand(roomId, chatMessage.text(), chatMessage.from());

            // 메시지 저장
            String chatId = chatMessageService.createChatMessage(chatMessageCreateCommand);

            // 저장된 메시지 응답
            return new ChatMessageResponse(chatId, chatMessage.text(), chatMessage.from());
        } catch (Exception e) {
            throw new RuntimeException("Error handling chat message", e);
        }
    }

    // HTTP POST 요청을 통한 메시지 전송 처리 (roomId는 고정 값)
    @PostMapping("/rooms/{roomId}/send")
    public ChatMessageResponse sendMessageHttp(@RequestBody ChatMessageRequest chatMessage) {
        try {
            // roomId는 고정 값 1
            Long roomId = 1L;

            // 채팅 메시지 생성
            ChatMessageCreateCommand chatMessageCreateCommand = new ChatMessageCreateCommand(roomId, chatMessage.text(), chatMessage.from());

            // 메시지 저장
            String chatId = chatMessageService.createChatMessage(chatMessageCreateCommand);

            // 저장된 메시지 응답
            return new ChatMessageResponse(chatId, chatMessage.text(), chatMessage.from());
        } catch (Exception e) {
            throw new RuntimeException("Error handling chat message", e);
        }
    }
    // 특정 채팅방의 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long roomId) {
        try {
            // 메시지 조회
            List<ChatMessageResponse> messages = chatMessageService.getMessagesByRoomId(roomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving chat messages", e);
        }
    }



    // 예외 처리 로직 (클라이언트에 에러 메시지 전송 등)
    @MessageExceptionHandler
    public void handleMessageException(RuntimeException e) {
        System.err.println(e.getMessage());
    }

    // HTTP 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
