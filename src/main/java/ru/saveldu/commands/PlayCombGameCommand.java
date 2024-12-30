package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class PlayCombGameCommand implements CommandHandler{


    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    private final int MAX_COMB_CHANGE_SIZE = 14;
    private final int MIN_COMB_CHANGE_SIZE = -9;

    public PlayCombGameCommand() {

    }

    @Override
    public void execute(Update update) throws SQLException {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        LocalDate today = LocalDate.now();
        String userName = update.getMessage().getFrom().getFirstName();
        String userNameToString = bot.formatUserMention(userName, userId);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.createQuery("from User where chatId = :chatId and userId = :userId", User.class)
                    .setParameter("chatId",chatId)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (user == null) {
                User newUser = new User();
                newUser.setUserId(userId);
                newUser.setChatId(chatId);
                newUser.setUserName(userName);
                newUser.setCombSize(0);
                session.save(newUser);
                transaction.commit();
                execute(update);

            } else {

                LocalDate lastPlayed = user.getLastPlayedDate();
                if (lastPlayed != null && lastPlayed.equals(today)) {
                    bot.sendMessage(chatId, BotMessages.ALREADY_PLAYED_COMB.format(userNameToString, user.getCombSize()));
                    transaction.commit();
                    return;
                }
                if (user.getCombSize() == null) {
                    user.setCombSize(0);
                }
                int currentCombSize = user.getCombSize();
                Random rand = new Random();
                int deltaSize = rand.nextInt(MAX_COMB_CHANGE_SIZE - MIN_COMB_CHANGE_SIZE + 1) + MIN_COMB_CHANGE_SIZE;
                int newCombSize = currentCombSize + deltaSize;
                user.setCombSize(newCombSize);
                user.setLastPlayedDate(today);
                session.saveOrUpdate(user);

                List<User> users = session.createQuery("from User where chatId = :chatId order by combSize desc", User.class)
                        .setParameter("chatId", chatId)
                        .list();
                int rank = 1;
                for (User u : users) {
                    if (u.getUserId() == userId) {
                        break;
                    }
                    rank++;
                }
                String rankMessage = " Ты сейчас на " + rank + " месте в группе.";

                transaction.commit();
                if (deltaSize > 0) {
                    bot.sendMessage(chatId, BotMessages.INCREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
                } else if (deltaSize < 0) {
                    bot.sendMessage(chatId, BotMessages.DECREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
                } else {
                    bot.sendMessage(chatId, BotMessages.NO_CHANGE_COMB.format(userNameToString, newCombSize) + rankMessage);
                }
            }

        }
    }
}
