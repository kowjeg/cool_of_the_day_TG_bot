package ru.saveldu.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.exceptions.COTDAlreadyChosen;
import ru.saveldu.exceptions.NoUserInChat;
import ru.saveldu.services.CoolOfTheDayService;
import ru.saveldu.services.MessageService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CoolOfTheDayCommandTest {


    @Mock
    private CoolOfTheDayService coolOfTheDayService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CoolOfTheDayCommand command;


    @Test
    void testExecute_COTDsuccess() {

        Update update = mock(Update.class);
        Message message = mock(Message.class);

        long chatId = 1L;
        User user = new User(1, chatId, 1L, "Boris", 5, LocalDate.now());


        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        when(coolOfTheDayService.chooseCoolOfTheDay(chatId)).thenReturn(user);

        command.execute(update);

        verify(messageService).sendCoolOfTheDayResult(chatId,"Boris",1L);

    }
    @Test
    void testExecute_COTDAlreadyChoosen(){
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        long chatId = 1L;

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(coolOfTheDayService.chooseCoolOfTheDay(chatId)).thenThrow(new COTDAlreadyChosen("already choosen"));

        command.execute(update);

        verify(messageService).sendMessage(chatId, "already choosen");

    }

    @Test
    void test_exequteThrowsNoUserInChatException() {

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        long chatId = 1L;

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(coolOfTheDayService.chooseCoolOfTheDay(chatId)).thenThrow(new NoUserInChat(BotMessages.NO_USER_IN_CHAT.format()));

        command.execute(update);


        verify(messageService).sendMessage(eq(chatId), contains("пользователь вышел из чата"));

    }


}