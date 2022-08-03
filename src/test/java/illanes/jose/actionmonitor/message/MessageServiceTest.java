package illanes.jose.actionmonitor.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import illanes.jose.actionmonitor.message.dto.MessageDTO;
import illanes.jose.actionmonitor.message.dto.MessageEventDTO;
import illanes.jose.actionmonitor.message.model.Message;
import illanes.jose.actionmonitor.message.model.MessageRepository;
import illanes.jose.actionmonitor.message.websocket.MessageWebsocketService;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    private static final String MESSAGE_CONTENT = "message content";

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageWebsocketService messageWebsocketService;

    @InjectMocks
    private MessageService messageService;

    private MessageDTO messageDTO;

    private static final int MESSAGE_ID = 1;

    @Nested
    class WhenCreateMessageIsInvoked {
        @Captor
        ArgumentCaptor<Message> messageCaptor;

        Message message;

        @BeforeEach
        void setUp() {
            messageDTO = MessageDTO.builder().messageContent(MESSAGE_CONTENT).build();
            message = Message.builder()
                    .messageContent(MESSAGE_CONTENT)
                    .build();

            when(messageRepository.save(message)).thenReturn(message);
        }

        @Test
        void theMessageIsStoredInTheDB() {
            messageService.createMessage(messageDTO);

            verify(messageRepository).save(messageCaptor.capture());
            assertThat(messageCaptor.getValue()).isEqualTo(message);
        }

        @Test
        void theMessageWebsocketServiceIsCalled() {
            var expectedMessageEventDTO = MessageEventDTO.builder()
                    .id(1)
                    .messageContent(MESSAGE_CONTENT)
                    .lastInteraction(ZonedDateTime.now())
                    .build();
            when(messageWebsocketService.convertMessageAndSend(message, false)).thenReturn(expectedMessageEventDTO);

            var messageEventDTO = messageService.createMessage(messageDTO);

            verify(messageWebsocketService).convertMessageAndSend(message, false);
            assertThat(messageEventDTO).isEqualTo(expectedMessageEventDTO);

        }
    }

    @Nested
    class WhenUpdateMessageIsInvoked {

        @Nested
        class AndMessageExists {

            @BeforeEach
            void setUp() {
                when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(Message.builder().id(MESSAGE_ID).build()));
                messageDTO = MessageDTO.builder().messageContent(MESSAGE_CONTENT).build();
            }

            @Test
            void thenMessageIsRetrievedById() {
                messageService.updateMessage(MESSAGE_ID, messageDTO);

                verify(messageRepository).findById(MESSAGE_ID);
            }

            @Test
            void theMessageWebsocketServiceIsCalled() {
                var expectedMessageEventDTO = MessageEventDTO.builder()
                        .id(1)
                        .messageContent(MESSAGE_CONTENT)
                        .lastInteraction(ZonedDateTime.now())
                        .build();

                when(messageRepository.findById(1)).thenReturn(Optional.of(Message.builder().id(1).build()));
                when(messageWebsocketService.convertMessageAndSend(any(Message.class), eq(true))).thenReturn(
                        expectedMessageEventDTO);

                var messageEventDTO = messageService.updateMessage(MESSAGE_ID, messageDTO);

                verify(messageWebsocketService).convertMessageAndSend(any(Message.class), eq(true));
                assertThat(messageEventDTO).isEqualTo(expectedMessageEventDTO);
            }
        }

        @Nested
        class AndMessageDoesNotExist {
            @Test
            void thenNotFoundExceptionIsThrown() {
                when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> messageService.updateMessage(MESSAGE_ID, messageDTO));
            }
        }
    }
}