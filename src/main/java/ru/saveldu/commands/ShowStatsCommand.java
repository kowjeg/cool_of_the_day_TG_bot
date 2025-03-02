package ru.saveldu.commands;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;

import ru.saveldu.entities.Stat;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.StatRepository;


import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Component
public class ShowStatsCommand implements CommandHandler {

    private final MyAmazingBot bot;
    private static final int TOP_COOLS_LIST = 10;
    private final StatRepository statRepository;


    @Autowired
    @Lazy
    public ShowStatsCommand(MyAmazingBot bot, StatRepository statRepository) {
        this.bot = bot;
        this.statRepository = statRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) throws SQLException {

        long chatId = update.getMessage().getChatId();
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        List<Stat> statsList = statRepository.findByChatIdAndYear(chatId,currentYear);

        StringBuilder statMessage = new StringBuilder(BotMessages.STATS_HEADER.format(String.valueOf(currentYear))).append("\n");
        for (Stat s : statsList) {
            statMessage.append(s.getUserName()).append(" - ").append(s.getCountWins()).append(" раз\n");
        }
        bot.sendMessage(chatId, statMessage.toString());
    }

    @Override
    public String getName() {
        return "stats2";
    }
}

