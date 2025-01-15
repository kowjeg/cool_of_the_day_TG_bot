package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final int MAX_LENGTH_USERNAME = 14;
    private static final int TOP_COMB_LIST_SIZE = 10;



    private static final Logger logger = LoggerFactory.getLogger(CombStatsCommand.class);

    public CombStatsCommand() {

    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder stringBuilder = new StringBuilder();
            String combStatsHql = "from User where chatId = :chatId and combSize is not null order by combSize desc";

            Query<User> queryList = session.createQuery(combStatsHql, User.class)
                    .setParameter("chatId", chatId)
                    .setMaxResults(TOP_COMB_LIST_SIZE);

            List<User> combSizesList = queryList.list();
            stringBuilder.append("`\nТоп 10 расчёсок:\n\n");

            stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
            stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());

            for (User u : combSizesList) {

                StringBuilder userNameActual = new StringBuilder(u.getUserName());
                if (userNameActual.length() > MAX_LENGTH_USERNAME) {
                    userNameActual = new StringBuilder(userNameActual.substring(0, MAX_LENGTH_USERNAME)).append("...");

                }
                stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), userNameActual, u.getCombSize()));
            }
            stringBuilder.append("`");
            bot.sendMessage(chatId, stringBuilder.toString());


        } catch (Exception e) {

            logger.error("Ошибка в методе выбора списка топ 10 расчесок: {}", chatId, e);
            throw new RuntimeException(e);

        }
    }
}
