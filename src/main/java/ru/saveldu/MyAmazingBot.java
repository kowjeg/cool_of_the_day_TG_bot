package ru.saveldu;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.saveldu.commands.AiChatHandler;
import ru.saveldu.commands.CommandHandler;
import ru.saveldu.commands.SummaryCommandHandler;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
//    private static final Logger logger = LoggerFactory.getLogger(MyAmazingBot.class);
    private final TelegramClient telegramClient;
    private Map<String, CommandHandler> commands;

    @Autowired
    private SummaryCommandHandler summaryCommandHandler;

    @Autowired
    private AiChatHandler aiChatHandler;

    public MyAmazingBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Autowired
    public void setCommands(List<CommandHandler> commandHandlers) {
        this.commands = commandHandlers.stream()
                .collect(Collectors.toMap(handler -> "/" + handler.getName().replace("CommandHandler", "").toLowerCase(),
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

    public void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendReplyMessage(long chatId, String text, int replyToMessageId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .replyToMessageId(replyToMessageId) //
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String formatUserMention(String usName, long userId) {
        // Проверяем, есть ли у победителя юзернейм
        String userName = getUserNameById(userId);
        if (userName != null) {

            return "@" + userName;
        }

        return "[" + usName + "](tg://user?id=" + userId + ")";
    }

    private String getUserNameById(long userId) {
        try {
            GetChat getChat = new GetChat(String.valueOf(userId));
            getChat.setChatId(userId);


            Chat chat = telegramClient.execute(getChat);

            return chat.getUserName();
        } catch (Exception e) {
//            logger.warn("Не удалось получить username для userId {}: {}", userId, e.getMessage());
            return null;
        }
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
//                logger.warn("Неизвестная команда: {}", rawCommand);
                sendMessage(chatId, "Неизвестная команда.");
            }

        } catch (Exception e) {
//            logger.error("Ошибка обработки сообщения: ", e);
        }
    }
}