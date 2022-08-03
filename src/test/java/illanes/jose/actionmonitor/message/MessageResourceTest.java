package illanes.jose.actionmonitor.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import illanes.jose.actionmonitor.message.dto.MessageDTO;
import illanes.jose.actionmonitor.message.dto.MessageEventDTO;

@ExtendWith(MockitoExtension.class)
class MessageResourceTest {

    private static final String MESSAGE_CONTENT = "message content";

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageResource messageResource;

    private MessageDTO messageDTO;

    private MessageEventDTO expectedMessageEventDTO;

    @BeforeEach
    void setUp() {
        messageDTO = buildMessageDTO();
        expectedMessageEventDTO = buildExpectedMessageEventDTO();
    }

    @Nested
    class WhenCreateMessageIsInvoked {
        @Test
        void thenMessageServiceIsCalled() {
            when(messageService.createMessage(messageDTO)).thenReturn(expectedMessageEventDTO);

            var messageEventDTO = messageResource.createMessage(messageDTO);

            verify(messageService).createMessage(messageDTO);
            assertThat(messageEventDTO).isEqualTo(expectedMessageEventDTO);
        }
    }

    @Nested
    class WhenUpdateMessageIsInvoked {
        @Test
        void thenMessageServiceIsCalled() {
            when(messageService.updateMessage(1, messageDTO)).thenReturn(expectedMessageEventDTO);

            var messageEventDTO = messageResource.updateMessage(1, messageDTO);

            verify(messageService).updateMessage(1, messageDTO);
            assertThat(messageEventDTO).isEqualTo(expectedMessageEventDTO);
        }
    }

    private MessageDTO buildMessageDTO() {
        return MessageDTO.builder()
                .messageContent(MESSAGE_CONTENT)
                .build();
    }

    private MessageEventDTO buildExpectedMessageEventDTO() {
        return MessageEventDTO.builder()
                .id(1)
                .messageContent(MESSAGE_CONTENT)
                .lastInteraction(ZonedDateTime.now())
                .build();
    }

}