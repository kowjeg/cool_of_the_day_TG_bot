package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.CoolOfTheDay;
import ru.saveldu.entities.Stat;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component

public class ChooseCoolOfTheDayCommand implements CommandHandler {


    private final MyAmazingBot bot;

    @Autowired
    @Lazy
    public ChooseCoolOfTheDayCommand(MyAmazingBot bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Update update) throws SQLException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate today = LocalDate.now();
            long chatId = update.getMessage().getChatId();
            String checkHql = "from CoolOfTheDay where chatId = :chatId and date = :date";
            CoolOfTheDay todayCoolOfTheDay = session.createQuery(checkHql, CoolOfTheDay.class)
                    .setParameter("chatId", chatId)
                    .setParameter("date", today)
                    .uniqueResult();
            if (todayCoolOfTheDay != null) {
                bot.sendMessage(chatId, BotMessages.COOL_DAY_ALREADY_CHOSEN.format(todayCoolOfTheDay.getUserName()));
                return;
            }

            String selectHql = "from User where chatId = :chatId";

            Transaction transaction = session.beginTransaction();
            Query<User> query = session.createQuery(selectHql, User.class)
                    .setParameter("chatId", chatId);
            List<User> userList = query.list();
            if (userList.isEmpty()) {
                return;
            }
            Random random = new Random();
            int participants = userList.size();
            int winnerNumber = random.nextInt(participants);
            User winner = userList.get(winnerNumber);

            //insert to cool_of_the_day

            CoolOfTheDay coolOfTheDay = new CoolOfTheDay();
            coolOfTheDay.setDate(today);
            coolOfTheDay.setUserId(winner.getUserId());
            coolOfTheDay.setChatId(winner.getChatId());
            coolOfTheDay.setUserName(winner.getUserName());
            session.save(coolOfTheDay);

            //insert to stats

            String hqlCheck = "FROM Stat WHERE chatId = :chatId AND userId = :userId AND year = :year";
            Stat existingStat = session.createQuery(hqlCheck, Stat.class)
                    .setParameter("chatId", winner.getChatId())
                    .setParameter("userId", winner.getUserId())
                    .setParameter("year", today.getYear())
                    .uniqueResult();
            if (existingStat != null) {
                existingStat.setCountWins(existingStat.getCountWins() + 1);
                session.update(existingStat);
            } else {
                Stat stat = new Stat();
                stat.setYear(today.getYear());
                stat.setUserId(winner.getUserId());
                stat.setUserName(winner.getUserName());
                stat.setChatId(winner.getChatId());
                stat.setCountWins(1);
                session.save(stat);
            }
            transaction.commit();

            bot.sendMessage(chatId, BotMessages.ANALYZING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.SELECTING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.CALIBRATING.format());
            Thread.sleep(700);
            String message = BotMessages.COOL_DAY_RESULT.format(bot.formatUserMention(winner.getUserName(), winner.getUserId()));
            bot.sendMessage(chatId, message);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getName() {
        return "cooloftheday";
    }
}
