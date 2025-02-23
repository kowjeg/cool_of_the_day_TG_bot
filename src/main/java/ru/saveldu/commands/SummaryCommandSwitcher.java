package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.enums.ChatApiType;

import java.io.IOException;
import java.sql.SQLException;

public class SummaryCommandSwitcher implements CommandHandler{

    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(SummaryCommandSwitcher.class);

    public void execute(Update update) throws SQLException {
        if(isUserAdmin(update.getMessage().getFrom().getId())) {
            if (SummaryCommandHandler.isActive()) {
                SummaryCommandHandler.setIsActive(false);
            } else {
                SummaryCommandHandler.setIsActive(true);
            }
            bot.sendMessage(update.getMessage().getChatId(), "Суммаризация переключена, текущий статус: " + SummaryCommandHandler.isActive());
            logger.info("Суммаризация переключена, текущий статус: " + SummaryCommandHandler.isActive());

        } else {
            bot.sendMessage(update.getMessage().getChatId(), "You are not admin");
        }

    }

    private boolean isUserAdmin(long usedId) {
        return usedId == 128697674;
    }
}
