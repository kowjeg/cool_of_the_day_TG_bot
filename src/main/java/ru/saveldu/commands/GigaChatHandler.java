package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;


import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.GigaChatApi;

public class GigaChatHandler implements CommandHandler {

    private GigaChatApi gigaChatApi = null;
    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    public GigaChatHandler() {
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
            bot.sendReplyMessage(chatId,answer, update.getMessage().getMessageId());


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
