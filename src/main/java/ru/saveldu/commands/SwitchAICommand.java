package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.enums.ChatApiType;

import java.sql.SQLException;

public class SwitchAICommand implements CommandHandler{

    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(SwitchAICommand.class);
    @Override
    public void execute(Update update) throws SQLException {
        if(isUserAdmin(update.getMessage().getFrom().getId())) {
            ChatApiType newApiType = AiChatHandler.switchApi();
            String newApiTypeString = newApiType.toString();
            bot.sendMessage(update.getMessage().getChatId(), "new API: " + newApiTypeString);
            logger.info("switch API: " + newApiTypeString);

        } else {
            bot.sendMessage(update.getMessage().getChatId(), "You are not admin");
        }

    }

    private boolean isUserAdmin(long usedId) {
        return usedId == 128697674;
    }
}
