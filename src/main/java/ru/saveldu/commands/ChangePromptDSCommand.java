package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.DeepSeekApi;

import java.sql.SQLException;

public class ChangePromptDSCommand implements CommandHandler{
    private static final Logger logger = LoggerFactory.getLogger(ChangePromptDSCommand.class);
    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();
    @Override
    public void execute(Update update) throws SQLException {
        if (update.getMessage().getFrom().getId()==128697674) {
            DeepSeekApi.setPrompt(update.getMessage().getText());
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText("Prompt updated");
            bot.sendMessage(update.getMessage().getChatId(),"Prompt updated!");
            logger.info("changed DeepSeek prompt. new prompt:" + update.getMessage().getText());

        }
        else {
            String message = "not admin";
            bot.sendMessage(update.getMessage().getChatId(),message);

        }


    }
}
