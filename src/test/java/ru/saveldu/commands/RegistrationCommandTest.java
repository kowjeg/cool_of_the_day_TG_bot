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
import ru.saveldu.services.RegistrationService;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationCommandTest {

    @Mock
    private MessageService messageService;
    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationCommand command;

    @Test
    void execute_UserAlreadyRegistered() {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);
        when(message.getChatId()).thenReturn(456L);
        when(user.getFirstName()).thenReturn("Tester");
        when(messageService.formatUserMention("Tester", 123L)).thenReturn("@Tester");
        when(registrationService.isUserAlreadyRegistered(123L, 456L)).thenReturn(true);

        command.execute(update);

        // Assert
        verify(messageService).sendMessage(eq(456L), contains("зарегистрирован"));

    }

    @Test
    void execute_RegisterUser() {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);
        when(message.getChatId()).thenReturn(456L);
        when(user.getFirstName()).thenReturn("Tester");
        when(messageService.formatUserMention("Tester", 123L)).thenReturn("@Tester");
        when(registrationService.isUserAlreadyRegistered(123L, 456L)).thenReturn(false);

        command.execute(update);

        // Assert



        verify(registrationService).registerUser(123L, 456L, "Tester");
        verify(messageService).sendMessage(eq(456L), contains("зарегистрировал"));

    }




}