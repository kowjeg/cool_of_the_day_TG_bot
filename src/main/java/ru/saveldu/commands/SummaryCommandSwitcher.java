package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;

import java.sql.SQLException;


@Component
public class SummaryCommandSwitcher implements CommandHandler{

    private final MyAmazingBot bot;
    private static final Logger logger = LoggerFactory.getLogger(SummaryCommandSwitcher.class);

    @Autowired
    @Lazy
    public SummaryCommandSwitcher(MyAmazingBot bot) {
        this.bot = bot;
    }
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

    @Override
    public String getName() {
        return "summaryswitch";
    }

    private boolean isUserAdmin(long usedId) {
        return usedId == 128697674;
    }
}
