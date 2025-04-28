package ru.saveldu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.saveldu.enums.BotMessages;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final TelegramClient telegramClient;

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

    @Override
    public void sendCoolOfTheDayResult(long chatId, String winnerUserName, long winnerUserId) {
        try {
            sendMessage(chatId, BotMessages.ANALYZING.format());
            Thread.sleep(700);
            sendMessage(chatId, BotMessages.SELECTING.format());
            Thread.sleep(700);
            sendMessage(chatId, BotMessages.CALIBRATING.format());
            Thread.sleep(700);
            String message = BotMessages.COOL_DAY_RESULT.format(formatUserMention(winnerUserName, winnerUserId));
            sendMessage(chatId, message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    public String formatUserMention(String firstName, long userId) {
        // Проверяем, есть ли у победителя юзернейм
        String userName = getUserNameById(userId);
        if (userName != null) {

            return "@" + userName;
        }

        return "[" + firstName + "](tg://user?id=" + userId + ")";
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
}