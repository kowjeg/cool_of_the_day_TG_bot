package ru.saveldu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.commands.AiChatHandler;
import ru.saveldu.commands.CommandHandler;
import ru.saveldu.commands.SummaryCommandHandler;
import ru.saveldu.services.MessageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final String botUsername;
    private final MessageService messageService;
    private final SummaryCommandHandler summaryCommandHandler;
    private final AiChatHandler aiChatHandler;
    private final Map<String, CommandHandler> commands;


    public MyAmazingBot(@Value("${bot.token}") String botToken,
                        @Value("${bot.username}") String botUsername,
                        List<CommandHandler> handlers,
                        MessageService messageService,
                        SummaryCommandHandler summaryCommandHandler,
                        AiChatHandler aiChatHandler) {
        this.botToken = botToken;               // <-- и сюда
        this.botUsername = botUsername;
        this.commands = handlers.stream()
                .collect(Collectors.toMap(handler -> "/" + handler.getName().toLowerCase(),
                        Function.identity()));
        this.messageService = messageService;
        this.summaryCommandHandler = summaryCommandHandler;
        this.aiChatHandler = aiChatHandler;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    boolean isReplyToBot(Message message) {

        return Optional.ofNullable(message.getReplyToMessage())
                .map(Message::getFrom)
                .map(User::getUserName)
                .filter(userName -> userName.equalsIgnoreCase(botUsername))
                .isPresent();
    }

    String[] parseCommand(String text) {

        String raw = text.split(" ", 2)[0];
        String[] parts = raw.split("@", 2);
        String name = parts[0].toLowerCase();
        String mentioned = parts.length > 1
                ? parts[1]
                : null;
        return new String[]{ name, mentioned };
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message message = update.getMessage();
        String messageText = message.getText();
        Long chatId = message.getChatId();
        boolean isPrivateChat = Optional.ofNullable(message.getChat())
                .map(c -> c.isUserChat())
                .orElse(false);

        try {
//            if no command - add to chat history
            if (!messageText.startsWith("/")) {
                summaryCommandHandler.addMessage(update);

                // if reply to bot message - execute on deepseek
                if (isReplyToBot(message)) {
                    aiChatHandler.execute(update);
                }
                return;
            }

            String[] cmd = parseCommand(messageText);
            String command = cmd[0];
            String mentionedBot = cmd[1];

            if (!isPrivateChat) {
                if (mentionedBot == null || !mentionedBot.equalsIgnoreCase(botUsername)) {
                    return;
                }
            }

            CommandHandler handler = commands.get(command);
            if (handler != null) {
                handler.execute(update);
            } else {
                log.warn("Unknown command '{}' from user {} in chat {}", command,
                        message.getFrom().getId(), chatId);

                messageService.sendMessage(chatId, "Неизвестная команда.");
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: ", e);
        }
    }

}