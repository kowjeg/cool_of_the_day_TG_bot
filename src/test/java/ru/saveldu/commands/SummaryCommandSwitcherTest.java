package ru.saveldu.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.services.MessageService;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SummaryCommandSwitcherTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private SummaryCommandSwitcher command;


    @Test
    void testExecute_switchIfAdmin() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        User user = mock(User.class);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(128697674L);
        when(message.getChatId()).thenReturn(128697674L);
        command.execute(update);

        verify(messageService).sendMessage(eq(128697674L),contains("Суммаризация переключена"));

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

        verify(messageService).sendMessage(anyLong(),contains("not admin"));

    }


}