package illanes.jose.actionmonitor.message;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import illanes.jose.actionmonitor.message.dto.MessageDTO;
import illanes.jose.actionmonitor.message.dto.MessageEventDTO;
import lombok.SneakyThrows;

@WebMvcTest(MessageResource.class)
class MessageResourceMVCTest {
    private static final String MESSAGE_ENDPOINT = "/messages";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    @Test
    @SneakyThrows
    void shouldReturnAMessage() {
        MessageDTO messageDTO = MessageDTO.builder().messageContent("a message").build();
        MessageEventDTO messageEventDTO = MessageEventDTO.builder()
                .id(1)
                .messageContent("a message")
                .lastInteraction(ZonedDateTime.now())
                .updated(false)
                .build();

        given(messageService.createMessage(messageDTO)).willReturn(messageEventDTO);

        mockMvc
                .perform(
                        post(MESSAGE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(messageDTO))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.messageContent", is("a message")))
                .andExpect(jsonPath("$.lastInteraction", is(notNullValue())))
                .andExpect(jsonPath("$.updated", is(false)));
    }
}


