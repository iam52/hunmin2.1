package com.hunmin.global.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.chatmessage.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate redisTemplate;

    //메세지를 구독자들에게 송신
    public void sendMessage(ChatMessageDTO publishMessage) {
        try {
            messagingTemplate.convertAndSend("/sub/chat/room/" + publishMessage.getChatRoomId(), publishMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
    //stomp이용해서 구독자들에게 송신
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            ChatMessageDTO chatMessage = objectMapper.readValue(publishMessage, ChatMessageDTO.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoomId(), chatMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
