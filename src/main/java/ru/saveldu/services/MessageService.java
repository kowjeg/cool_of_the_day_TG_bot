package ru.saveldu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.enums.BotMessages;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MyAmazingBot bot;

    public void sendCoolOfTheDayResult(long chatId, String winnerUserName, long winnerUserId) {
        try {
            bot.sendMessage(chatId, BotMessages.ANALYZING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.SELECTING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.CALIBRATING.format());
            Thread.sleep(700);
            String message = BotMessages.COOL_DAY_RESULT.format(bot.formatUserMention(winnerUserName, winnerUserId));
            bot.sendMessage(chatId, message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendMessage(long chatId, String message) {
        String mess = message;
        bot.sendMessage(chatId, message);
    }
}