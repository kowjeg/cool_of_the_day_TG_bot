package ru.saveldu.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.entities.Users;
import ru.saveldu.repositories.UserRepository;
import ru.saveldu.services.MessageService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CombStatsCommandTest {

    @Mock
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CombStatsCommand command;

    @Test
    void execute_shouldSendCorrectCombStatsMessage () {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        long chatId = 123L;

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        List<Users> combSizesList = List.of(
                new Users(1,chatId, 1L, "Alex", 3, LocalDate.now()),
                new Users(2,chatId, 2L, "Henry", 4, LocalDate.now())

        );
        when(userRepository.findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(chatId)).thenReturn(combSizesList);


        command.execute(update);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(messageService).sendMessage(eq(chatId),captor.capture());

        String resultString = captor.getValue();

        assertTrue(resultString.contains(String.format("%-18s %5s%n", "Alex", 3)));
        assertTrue(resultString.contains(String.format("%-18s %5s%n", "Henry", 4)));

    }

    @Test
    void execute_shouldTruncateLongUserName() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(1L);

        Users user = new Users();
        user.setUserName("superlongusername420420420");

        when(userRepository.findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(1L))
                .thenReturn(List.of(user));

        command.execute(update);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(messageService).sendMessage(eq(1L), captor.capture());
        String sent = captor.getValue();

        assertTrue(sent.contains("superlongusern..."));

    }
}