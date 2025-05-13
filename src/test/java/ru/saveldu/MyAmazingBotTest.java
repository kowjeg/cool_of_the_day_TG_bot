package ru.saveldu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.commands.AiChatHandler;
import ru.saveldu.commands.CommandHandler;
import ru.saveldu.commands.SummaryCommandHandler;
import ru.saveldu.services.MessageService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyAmazingBotTest {

    // Общие поля
    private final String botToken = "testBotToken";
    private final String botUsername = "testBotUsername";

    private SummaryCommandHandler summaryHandler;
    private AiChatHandler aiChatHandler;
    private MessageService messageService;
    private List<CommandHandler> commands;
    private MyAmazingBot bot;

    @BeforeEach
    void setUp() {
        // Инициализируем все моки и сами объекты
        MockitoAnnotations.openMocks(this);

        summaryHandler = mock(SummaryCommandHandler.class);
        aiChatHandler = mock(AiChatHandler.class);
        messageService = mock(MessageService.class);
        commands = new ArrayList<>();

        bot = new MyAmazingBot(
                botToken,
                botUsername,
                commands,
                messageService,
                summaryHandler,
                aiChatHandler
        );
    }

    @Test
    void testIsReplyToBot_True() {
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(user.getUserName()).thenReturn(botUsername);
        when(message.getReplyToMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);

        assertTrue(bot.isReplyToBot(message));
    }

    @Test
    void testIsReplyToBot_False_NullReply() {
        Message message = mock(Message.class);
        when(message.getReplyToMessage()).thenReturn(null);

        assertFalse(bot.isReplyToBot(message));
    }

    @Test
    void testIsReplyToBot_False_DifferentUser() {
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(user.getUserName()).thenReturn("otherUsername");
        when(message.getReplyToMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);

        assertFalse(bot.isReplyToBot(message));
    }

    @Test
    void testParseCommand_ValidCommand() {
        String[] result = bot.parseCommand("/play@testBotUsername");

        assertEquals("/play", result[0]);
        assertEquals("testBotUsername", result[1]);
    }

    @Test
    void testParseCommand_CommandWithoutMention() {
        String[] result = bot.parseCommand("/help");

        assertEquals("/help", result[0]);
        assertNull(result[1]);
    }

    @Test
    void testConsume_NoMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);

        bot.consume(update);

        verify(summaryHandler, never()).addMessage(any());
        verify(aiChatHandler, never()).execute(any());
    }

    @Test
    void testConsume_NoTextMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        bot.consume(update);

        verify(summaryHandler, never()).addMessage(any());
        verify(aiChatHandler, never()).execute(any());
    }

    @Test
    void testConsume_AddMessageToHistory() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("Hello");

        bot.consume(update);

        verify(summaryHandler).addMessage(update);
        verify(aiChatHandler, never()).execute(any());
    }

    @Test
    void testConsume_ReplyToBot() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Message replyMessage = mock(Message.class);
        User user = mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("Hello");
        when(message.getReplyToMessage()).thenReturn(replyMessage);
        when(replyMessage.getFrom()).thenReturn(user);
        when(user.getUserName()).thenReturn(botUsername);

        bot.consume(update);

        verify(aiChatHandler).execute(update);
    }

    @Test
    void testConsume_UnknownCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/unknownCommand");
        when(message.getFrom()).thenReturn(mock(User.class));

        bot.consume(update);

        verify(aiChatHandler, never()).execute(any());
    }

    @Test
    void testConsume_GroupChat_NoMention() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/play");
        when(message.getChat()).thenReturn(chat);
        when(chat.isUserChat()).thenReturn(false);

        bot.consume(update);

        verify(summaryHandler, never()).addMessage(any());
        verify(aiChatHandler, never()).execute(any());
    }
}