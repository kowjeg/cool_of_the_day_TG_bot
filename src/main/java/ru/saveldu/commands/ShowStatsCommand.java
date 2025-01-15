package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.Stat;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ShowStatsCommand implements CommandHandler{

    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();
    private static final int TOP_COOLS_LIST = 10;

    public ShowStatsCommand() {
    }
    @Override
    public void execute(Update update) throws SQLException {


        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long chatId = update.getMessage().getChatId();
            LocalDate today = LocalDate.now();
            int currentYear = today.getYear();

            String statsHql = "FROM Stat where chatId = :chatId AND year = :year order by countWins desc";
            List<Stat> statList = session.createQuery(statsHql,Stat.class)
                    .setParameter("chatId", chatId)
                    .setParameter("year", currentYear)
                    .setMaxResults(TOP_COOLS_LIST)
                    .list()
                    ;

            StringBuilder statMessage = new StringBuilder(BotMessages.STATS_HEADER.format(String.valueOf(currentYear))).append("\n");
//            int participants = statList.size();
            for (Stat s : statList) {
                statMessage.append(s.getUserName()).append(" - ").append(s.getCountWins()).append(" раз\n");

            }
//            statMessage.append("\nВсего фолофанов: " + participants);
            bot.sendMessage(chatId,statMessage.toString());
        }
    }
}
