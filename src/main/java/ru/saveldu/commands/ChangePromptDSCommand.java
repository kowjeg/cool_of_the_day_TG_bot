package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.DeepSeekApi;

import java.sql.SQLException;

@Component

public class ChangePromptDSCommand implements CommandHandler{
    private static final Logger logger = LoggerFactory.getLogger(ChangePromptDSCommand.class);


    @Autowired
    @Lazy
    public ChangePromptDSCommand(MyAmazingBot bot) {
        this.bot = bot;
    }

    @Lazy
    private final MyAmazingBot bot;
    @Override
    public void execute(Update update) throws SQLException {
        if (update.getMessage().getFrom().getId()==128697674) {
            DeepSeekApi.setPrompt(update.getMessage().getText());
            bot.sendMessage(update.getMessage().getChatId(),"Prompt updated!");
            logger.info("changed DeepSeek prompt. new prompt:" + update.getMessage().getText());

        }
        else {
            String message = "not admin";
            bot.sendMessage(update.getMessage().getChatId(),message);

        }


    }

    @Override
    public String getName() {
        return "prompt";
    }
}
