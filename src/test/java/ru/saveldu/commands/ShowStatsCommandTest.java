package ru.saveldu.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.entities.Stat;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.StatRepository;
import ru.saveldu.services.MessageService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowStatsCommandTest {

    @Mock
    private MessageService messageService;

    @Mock
    private StatRepository statRepository;

    @InjectMocks
    private ShowStatsCommand command;

    @Test
    void execute_ShouldSendCorrectStatsMessage() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        long chatId = 123L;
        int currentYear = LocalDate.now().getYear();

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        List<Stat> stats = List.of(
                new Stat(1, chatId, 1L, "Alice", currentYear, 3),
                new Stat(2, chatId, 2L, "Bob", currentYear, 5)

        );
        when(statRepository.findByChatIdAndYear(chatId, currentYear)).thenReturn(stats);

        command.execute(update);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(messageService).sendMessage(eq(chatId), captor.capture());
        String sentMessage = captor.getValue();

        assertTrue(sentMessage.contains(BotMessages.STATS_HEADER.format(String.valueOf(currentYear))));
        assertTrue(sentMessage.contains("Alice - 3 раз"));
        assertTrue(sentMessage.contains("Bob - 5 раз"));
    }
}