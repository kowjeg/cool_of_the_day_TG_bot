package ru.saveldu.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.saveldu.entities.CoolOfTheDay;
import ru.saveldu.entities.Stat;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.exceptions.COTDAlreadyChosen;
import ru.saveldu.exceptions.NoUserInChat;
import ru.saveldu.repositories.CoolOfTheDayRepository;
import ru.saveldu.repositories.StatRepository;
import ru.saveldu.repositories.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoolOfTheDayServiceTest {

    @Mock
    private CoolOfTheDayRepository coolOfTheDayRepository;
    @Mock
    private StatRepository statRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CoolOfTheDayService service;

    @Test
    void already_Chosen_COTD() {
        long chatId = 123L;
        LocalDate today = LocalDate.now();
        CoolOfTheDay cotd = new CoolOfTheDay();
        when(coolOfTheDayRepository.findByChatIdAndDate(chatId, today)).thenReturn(Optional.of(cotd));
        assertThrows(COTDAlreadyChosen.class, () -> service.chooseCoolOfTheDay(chatId));

    }

    @Test
    void throw_If_No_Registered_Users_In_Chat() {
        long chatId = 123L;
        LocalDate today = LocalDate.now();
        when(coolOfTheDayRepository.findByChatIdAndDate(chatId, today)).thenReturn(Optional.empty());
        when(userRepository.getUsersByChatId(chatId)).thenReturn(Collections.emptyList());
        assertThrows(NoUserInChat.class, () -> service.chooseCoolOfTheDay(chatId));

    }

    @Test
    void choose_Winner_First_Time_New_Stats_Line() {
        long chatId = 123L;
        LocalDate today = LocalDate.now();
        User user = new User();
        user.setUserId(1L);
        user.setUserName("FirstTimeWinner");
        user.setChatId(chatId);

        when(coolOfTheDayRepository.findByChatIdAndDate(chatId, today)).thenReturn(Optional.empty());
        when(userRepository.getUsersByChatId(chatId)).thenReturn(List.of(user));
        when(statRepository.findByChatIdAndUserIdAndYear(chatId, user.getUserId(), today.getYear())).thenReturn(Optional.empty());

        User winner = service.chooseCoolOfTheDay(chatId);

        assertEquals(user, winner);
        verify(coolOfTheDayRepository).save(any(CoolOfTheDay.class));
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository).save(statCaptor.capture());
        assertEquals(1, statCaptor.getValue().getCountWins());
        assertEquals(user.getUserId(), statCaptor.getValue().getUserId());
    }

    @Test
    void choose_Winner_Existing_Stat_Line() {
        long chatId = 123L;
        LocalDate today = LocalDate.now();
        User user = new User();
        user.setUserId(1L);
        user.setUserName("AlreadyWinsUser");
        user.setChatId(chatId);

        Stat stat = new Stat();
        stat.setUserId(user.getUserId());
        stat.setUserName(user.getUserName());
        stat.setChatId(chatId);
        stat.setYear(today.getYear());
        stat.setCountWins(3);

        when(coolOfTheDayRepository.findByChatIdAndDate(chatId, today)).thenReturn(Optional.empty());
        when(userRepository.getUsersByChatId(chatId)).thenReturn(List.of(user));
        when(statRepository.findByChatIdAndUserIdAndYear(chatId, user.getUserId(), today.getYear())).thenReturn(Optional.of(stat));

        User winner = service.chooseCoolOfTheDay(chatId);

        assertEquals(user, winner);

        assertEquals(4, stat.getCountWins());
    }

    @Test
    void shouldChooseRandomWinnerFromMultipleUsers() {
        long chatId = 123L;
        LocalDate today = LocalDate.now();
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUserName("User1");
        user1.setChatId(chatId);

        User user2 = new User();
        user2.setUserId(2L);
        user2.setUserName("User2");
        user2.setChatId(chatId);

        when(coolOfTheDayRepository.findByChatIdAndDate(chatId, today)).thenReturn(Optional.empty());
        when(userRepository.getUsersByChatId(chatId)).thenReturn(List.of(user1, user2));
        when(statRepository.findByChatIdAndUserIdAndYear(anyLong(), anyLong(), anyInt())).thenReturn(Optional.empty());


        User winner = service.chooseCoolOfTheDay(chatId);

        assertTrue(List.of(user1, user2).contains(winner));

    }
}