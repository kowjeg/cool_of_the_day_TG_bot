package ru.saveldu.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.api.ChatApi;
import ru.saveldu.enums.ChatApiType;
import ru.saveldu.services.MessageService;

import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AiChatHandlerTest {
    @Mock
    private ChatApi gigaChatApi;
    @Mock
    private ChatApi deepSeekApi;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private AiChatHandler aiChatHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiChatHandler = new AiChatHandler(gigaChatApi, deepSeekApi, messageService);
    }

    @Test
    void testSwitchApi() {
        assertEquals(ChatApiType.GIGACHAT, aiChatHandler.switchApi());
        assertEquals(ChatApiType.DEEPSEEK, aiChatHandler.switchApi());
    }

    @Test
    void testGetCurrentChatApi() {

        assertSame(deepSeekApi, aiChatHandler.getCurrentChatApi());
        aiChatHandler.switchApi();
        assertSame(gigaChatApi, aiChatHandler.getCurrentChatApi());
    }

    @Test
    void testExecuteSuccess() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);
        when(message.getMessageId()).thenReturn(1);
        when(deepSeekApi.sendTextRequest(anyString(), eq(update))).thenReturn("ok");

        aiChatHandler.execute(update);
        verify(messageService).sendReplyMessage(123L, "ok", 1);
    }

    @Test
    void testExecuteException() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);
        when(message.getMessageId()).thenReturn(1);
        when(deepSeekApi.sendTextRequest(anyString(), eq(update))).thenThrow(new RuntimeException("fail"));

        aiChatHandler.execute(update);
        verify(messageService).sendMessage(eq(123L), contains("Нет настроения"));
    }

    @Test
    void testGetName() {
        assertEquals("aichat", aiChatHandler.getName());
    }
}
