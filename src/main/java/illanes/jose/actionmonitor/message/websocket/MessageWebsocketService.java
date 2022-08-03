package illanes.jose.actionmonitor.message.websocket;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import illanes.jose.actionmonitor.message.dto.MessageEventDTO;
import illanes.jose.actionmonitor.message.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageWebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Clock clock;

    public MessageEventDTO convertMessageAndSend(Message message, boolean updated) {
        var messageEventDTO = messageMapper(message, updated);
        log.info("Sending message to the topic: {}", messageEventDTO);
        messagingTemplate.convertAndSend("/topic/entries", messageEventDTO);
        return messageEventDTO;
    }

    private MessageEventDTO messageMapper(Message message, boolean updated) {
        return MessageEventDTO.builder()
                .id(message.getId())
                .messageContent(message.getMessageContent())
                .lastInteraction(ZonedDateTime.now(clock))
                .updated(updated)
                .build();
    }
}
