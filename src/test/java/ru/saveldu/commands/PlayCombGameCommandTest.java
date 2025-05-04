package ru.saveldu.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.entities.Users;
import ru.saveldu.repositories.UserRepository;
import ru.saveldu.services.MessageService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.*;


class PlayCombGameCommandTest {


    @Mock
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PlayCombGameCommand command;

    private Update update;
    private Message message;

    private User user;

    private long chatId = 123L;
    private long userId = 333L;


    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        update = mock(Update.class);
        message = mock(Message.class);
        user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);

        when(message.getChatId()).thenReturn(chatId);
        when(user.getId()).thenReturn(userId);
        when(user.getFirstName()).thenReturn("Tester");
        when(messageService.formatUserMention("Tester", userId)).thenReturn("@Tester");
        when(userRepository.save(any(Users.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }


    @Test
    void testExecute_UserDoingFirstPlayToday() {
        LocalDate today = LocalDate.now();
        Users userEntity = new Users(1, chatId, userId, "Tester", 3, null);
        when(userRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(userEntity));

        when(userRepository.findByChatIdOrderByCombSizeDesc(chatId))
                .thenReturn(List.of(userEntity));


        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(10);
        command.setRandom(random);

        command.execute(update);

        verify(messageService, never()).sendMessage(
                eq(chatId),
                contains("уже трогал")
        );

        verify(messageService).sendMessage(
                eq(chatId),
                contains("увеличил")
        );

    }

    @Test
    void testExecute_UserAlreadyPlayerTodayReturn() {
        LocalDate today = LocalDate.now();

        Users userEntity = new Users(1, chatId, userId, "Tester", 3, today);
        when(userRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(userEntity));

        command.execute(update);

        verify(messageService).sendMessage(eq(chatId), contains("уже трогал"));
    }

    @Test
    void testExecute_CombSizeIncreased() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(14); // 14 - 9 = 5
        command.setRandom(random);

        Users userEntity = new Users(1, chatId, userId, "Tester", 3, null);
        when(userRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(userEntity));

        command.execute(update);

        verify(messageService).sendMessage(eq(chatId), contains("увеличил"));
    }


    @Test
    void testExecute_CombSizeDecreased() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(5); // 5 - 9 = -4
        command.setRandom(random);

        Users userEntity = new Users(1, chatId, userId, "Tester", 3, null);
        when(userRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(userEntity));

        command.execute(update);

        verify(messageService).sendMessage(eq(chatId), contains("уменьшил"));
    }

    @Test
    void testExecute_CombSizeNoChange() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(9); // 9 - 9 = 0
        command.setRandom(random);

        Users userEntity = new Users(1, chatId, userId, "Tester", 3, null);
        when(userRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(userEntity));

        command.execute(update);

        verify(messageService).sendMessage(eq(chatId), contains("не поменялся"));
    }

    @Test
    void testExecute_NewUserCreatedAndSaved() {

        when(userRepository.findByChatIdAndUserId(chatId, userId))
                .thenReturn(Optional.empty());
        command.execute(update);
        verify(userRepository, times(2)).save(argThat(u ->
                u.getChatId() == chatId &&
                        u.getUserId() == userId &&
                        u.getLastPlayedDate() != null
        ));

    }

}