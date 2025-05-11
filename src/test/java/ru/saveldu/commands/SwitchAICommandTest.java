package ru.saveldu.commands;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.enums.ChatApiType;
import ru.saveldu.services.MessageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class SwitchAICommandTest {

    @Mock
    private MessageService messageService;

    @Mock
    private AiChatHandler aiChatHandler;

    @InjectMocks
    private SwitchAICommand command;

    @Test
    void testExecute_switchAi_ifUserIsAdmin() {

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        User user = mock(User.class);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(128697674L);
        when(message.getChatId()).thenReturn(128697674L);
        when(aiChatHandler.switchApi())
                .thenReturn(ChatApiType.DEEPSEEK);

        command.execute(update);

        verify(messageService).sendMessage(eq(128697674L),contains("New API"));

    }

    @Test
    void testExecute_switchIfNotAdmin() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        User user = mock(User.class);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);
        when(message.getChatId()).thenReturn(123L);
        command.execute(update);

        verify(messageService).sendMessage(anyLong(),contains("not an admin."));

    }



}