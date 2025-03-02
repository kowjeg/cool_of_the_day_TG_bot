package ru.saveldu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.GigaChatApi;

@Component
public class GigaChatHandler implements CommandHandler {

    private GigaChatApi gigaChatApi = null;
    private final MyAmazingBot bot;

    @Autowired
    @Lazy
    public GigaChatHandler(MyAmazingBot bot) {
        this.bot = bot;
        try {
            this.gigaChatApi = new GigaChatApi();
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            String answer = gigaChatApi.sendTextRequest(String.valueOf(chatId), update);
            bot.sendReplyMessage(chatId, answer, update.getMessage().getMessageId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
