package ru.saveldu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.commands.AiChatHandler;
import ru.saveldu.commands.CommandHandler;
import ru.saveldu.commands.SummaryCommandHandler;
import ru.saveldu.services.MessageService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private Map<String, CommandHandler> commands;
    private final MessageService messageService;
    private final SummaryCommandHandler summaryCommandHandler;
    private final AiChatHandler aiChatHandler;

    @Autowired
    public void setCommands(List<CommandHandler> commandHandlers) {
        this.commands = commandHandlers.stream()
                .collect(Collectors.toMap(handler -> "/" + handler.getName().toLowerCase(),
                        Function.identity()));
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message message = update.getMessage();
        String messageText = message.getText();
        Long chatId = message.getChatId();
            boolean isPrivateChat = message.getChat().isUserChat(); // group or private chat
        try {
//            if no command - add to chat history
            if (!messageText.startsWith("/")) {
                summaryCommandHandler.addMessage(update);
                // if reply to bot message - execute on deepseek
                System.out.println(message.getReplyToMessage().getFrom().getUserName());
                if (message.isReply() && message.getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(System.getenv("BOT_USERNAME"))) {
                    aiChatHandler.execute(update);
                }

                return;
            }

            String[] parts = messageText.split(" ", 2); // ["/play@buzzcatbot"]
            String rawCommand = parts[0]; // "/play@buzzcatbot"
            String[] commandParts = rawCommand.split("@");

            String command = commandParts[0]; // "/play"
            String mentionedBot = (commandParts.length > 1) ? commandParts[1] : null;

            if (!isPrivateChat) {
                if (mentionedBot == null || !mentionedBot.equalsIgnoreCase(System.getenv("BOT_USERNAME"))) {
                    return;
                }
            }
            // Проверяем, есть ли такая команда в списке
            CommandHandler handler = commands.get(command);
            if (handler != null) {
                handler.execute(update);
            } else {
                log.warn("Неизвестная команда: {}", rawCommand);
                messageService.sendMessage(chatId, "Неизвестная команда.");
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: ", e);
        }
    }

}