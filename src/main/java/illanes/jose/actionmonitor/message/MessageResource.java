package illanes.jose.actionmonitor.message;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import illanes.jose.actionmonitor.message.dto.MessageDTO;
import illanes.jose.actionmonitor.message.dto.MessageEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/messages")
@RequiredArgsConstructor
public class MessageResource {

    private final MessageService messageService;

    @PostMapping
    public MessageEventDTO createMessage(@RequestBody @Valid MessageDTO messageDTO) {
        log.info("Request to create the message {} received", messageDTO);
        return messageService.createMessage(messageDTO);
    }

    @PutMapping("/{id}")
    public MessageEventDTO updateMessage(@PathVariable Integer id, @RequestBody @Valid MessageDTO messageDTO) {
        log.info("Request to update the message with id:{} and content: {} received", id, messageDTO);
        return messageService.updateMessage(id, messageDTO);
    }
}
