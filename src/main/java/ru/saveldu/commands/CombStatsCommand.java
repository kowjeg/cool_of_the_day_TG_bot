package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.Stat;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;

import java.sql.*;
import java.util.List;

public class CombStatsCommand implements CommandHandler {
    private final MultiSessionTelegramBot bot = MyAmazingBot.getInstance();

    public CombStatsCommand() {

    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder stringBuilder = new StringBuilder();
            String combStatsHql = "from User where chatId = :chatId and combSize is not null order by combSize desc";

            Query<User> queryList = session.createQuery(combStatsHql, User.class)
                    .setParameter("chatId", chatId);

            List<User> combSizesList = queryList.list();
            stringBuilder.append(BotMessages.COMB_STATS_HEADER.format());
            stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
            stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());

            for (User u : combSizesList) {
                stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), u.getUserName(), u.getCombSize()));
            }
            bot.sendMessage(chatId, stringBuilder.toString());


        }
    }
}
