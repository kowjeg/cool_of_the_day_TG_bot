package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
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

public class PlayCombGameCommand implements CommandHandler {


    private final MultiSessionTelegramBot bot = MyAmazingBot.getInstance();

    private static final int MAX_COMB_CHANGE_SIZE = 14;
    private static final int MIN_COMB_CHANGE_SIZE = -9;

    public PlayCombGameCommand() {}

    @Override
    public void execute(Update update) throws SQLException {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        LocalDate today = LocalDate.now();
        String userName = update.getMessage().getFrom().getFirstName();
        String userNameToString = bot.formatUserMention(userName, userId);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = getOrCreateUser(session, chatId, userId, userName, transaction);

            LocalDate lastPlayed = user.getLastPlayedDate();
            if (lastPlayed != null && lastPlayed.equals(today)) {
                bot.sendMessage(chatId, BotMessages.ALREADY_PLAYED_COMB.format(userNameToString, user.getCombSize()));
                transaction.commit();
                return;
            }
            if (user.getCombSize() == null) {
                user.setCombSize(0);
            }


            Random rand = new Random();
            int deltaSize = rand.nextInt(MAX_COMB_CHANGE_SIZE - MIN_COMB_CHANGE_SIZE + 1) + MIN_COMB_CHANGE_SIZE;
            int newCombSize = user.getCombSize() + deltaSize;
            user.setCombSize(newCombSize);
            user.setLastPlayedDate(today);
            session.saveOrUpdate(user);

            List<User> users = session.createQuery("from User where chatId = :chatId order by combSize desc", User.class)
                    .setParameter("chatId", chatId)
                    .list();
            transaction.commit();

            int rank = calculateUserRank(users, userId);
            sendMessageBasedOnDelta(rank, deltaSize, chatId, userNameToString, newCombSize);


        }
    }

    @Nullable
    private User getOrCreateUser(Session session, long chatId, long userId, String userName, Transaction transaction) throws SQLException {
        User user = session.createQuery("from User where chatId = :chatId and userId = :userId", User.class)
                .setParameter("chatId", chatId)
                .setParameter("userId", userId)
                .uniqueResult();

        if (user == null) {
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setChatId(chatId);
            newUser.setUserName(userName);
            newUser.setCombSize(0);
            session.save(newUser);
            return newUser;

        }
        return user;
    }

    private static int calculateUserRank(List<User> users, long userId) {
        int rank = 1;
        for (User u : users) {
            if (u.getUserId() == userId) {
                break;
            }
            rank++;
        }
        return rank;
    }

    private void sendMessageBasedOnDelta(int rank, int deltaSize, long chatId, String userNameToString, int newCombSize) {

        String rankMessage = BotMessages.CURRENT_RANK.format(rank);
        if (deltaSize > 0) {
            bot.sendMessage(chatId, BotMessages.INCREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
        } else if (deltaSize < 0) {
            bot.sendMessage(chatId, BotMessages.DECREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
        } else {
            bot.sendMessage(chatId, BotMessages.NO_CHANGE_COMB.format(userNameToString, newCombSize) + rankMessage);
        }
    }
}
