package illanes.jose.actionmonitor.message;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import illanes.jose.actionmonitor.message.dto.MessageDTO;
import illanes.jose.actionmonitor.message.dto.MessageEventDTO;
import illanes.jose.actionmonitor.message.model.Message;
import illanes.jose.actionmonitor.message.model.MessageRepository;
import illanes.jose.actionmonitor.message.websocket.MessageWebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    private final MessageWebsocketService messageWebsocketService;

    public MessageEventDTO createMessage(MessageDTO messageDTO) {
        var savedMessage = messageRepository.save(buildMessage(messageDTO));
        log.info("Message {} has been saved in the db", savedMessage);
        return messageWebsocketService.convertMessageAndSend(savedMessage, false);
    }

    @Transactional
    public MessageEventDTO updateMessage(int messageId, MessageDTO messageDTO) {
        var message = messageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new);
        message.setMessageContent(messageDTO.getMessageContent());
        log.info("Message {} has been updated in the db", message);
        return messageWebsocketService.convertMessageAndSend(message, true);
    }

    private Message buildMessage(MessageDTO messageDTO) {
        return Message.builder()
                .messageContent(messageDTO.getMessageContent())
                .build();
    }
}