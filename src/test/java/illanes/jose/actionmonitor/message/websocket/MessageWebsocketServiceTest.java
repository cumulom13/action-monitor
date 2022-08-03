package illanes.jose.actionmonitor.message.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import illanes.jose.actionmonitor.message.model.Message;

@ExtendWith(MockitoExtension.class)
class MessageWebsocketServiceTest {

    private static final String TEST_INSTANT = "2022-06-06T10:15:30.00Z";

    private static final String MESSAGE_CONTENT = "message content";

    @InjectMocks
    private MessageWebsocketService messageWebsocketService;

    @Mock
    private Clock clock;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Nested
    class WhenConvertMessageAndSendIsInvoked {

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void thenMessageIsSent(boolean isUpdated) {
            when(clock.instant()).thenReturn(Instant.parse(TEST_INSTANT));
            when(clock.getZone()).thenReturn(ZoneId.systemDefault());

            var message = Message.builder()
                    .id(1)
                    .messageContent(MESSAGE_CONTENT)
                    .build();

            var messageEventDTO = messageWebsocketService.convertMessageAndSend(message, isUpdated);

            verify(messagingTemplate).convertAndSend("/topic/entries", messageEventDTO);
            assertThat(messageEventDTO.getId()).isEqualTo(1);
            assertThat(messageEventDTO.getMessageContent()).isEqualTo(MESSAGE_CONTENT);
            assertThat(messageEventDTO.getLastInteraction()).isEqualTo(TEST_INSTANT);
            assertThat(messageEventDTO.isUpdated()).isEqualTo(isUpdated);
        }
    }
}