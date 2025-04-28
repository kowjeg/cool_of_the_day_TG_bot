package ru.saveldu.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import org.telegram.telegrambots.meta.api.objects.chat.ChatFullInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private TelegramClient telegramClient;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void testSendMessage_CallsExecuteWithCorrectMessage() throws Exception {
        long chatId = 123L;
        String text = "Hello";
        messageService.sendMessage(chatId, text);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient).execute(captor.capture());
        SendMessage sent = captor.getValue();
        assertEquals(String.valueOf(chatId), sent.getChatId());
        assertEquals(text, sent.getText());

    }

    @Test
    void testFormatUserMention_WithUsername() throws TelegramApiException {

        ChatFullInfo chat = mock(ChatFullInfo.class);
        when(chat.getUserName()).thenReturn("testusername");
        when(telegramClient.execute(any(GetChat.class))).thenReturn(chat);

        String result = messageService.formatUserMention("Aleks", 123L);

        assertEquals("@testusername", result);
    }

    @Test
    void testFormatUserMention_WithoutUsername() throws Exception {

        ChatFullInfo chat = mock(ChatFullInfo.class);
        when(chat.getUserName()).thenReturn(null);
        when(telegramClient.execute(any(GetChat.class))).thenReturn(chat);

        String result = messageService.formatUserMention("Aleks", 123L);
        assertTrue(result.contains("tg://user?id=123"));
    }


}
