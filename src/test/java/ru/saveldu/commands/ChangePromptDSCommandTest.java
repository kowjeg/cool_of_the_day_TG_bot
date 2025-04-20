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
import java.sql.SQLException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePromptDSCommandTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChangePromptDSCommand command;

    @Test
    void update_Prompt_if_Admin() throws SQLException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(128697674L); // admin ID
        when(message.getText()).thenReturn("new prompt text");
        when(message.getChatId()).thenReturn(123L);

        command.execute(update);

        verify(messageService).sendMessage(123L, "Prompt updated!");
    }

    @Test
    void deny_Change_Prompt_if_Not_Admin() throws SQLException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(999L); // non-admin ID
        when(message.getChatId()).thenReturn(123L);

        command.execute(update);

        verify(messageService).sendMessage(123L, "not admin");
    }

}