package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.Stat;
import ru.saveldu.enums.BotMessages;

import java.sql.*;
import java.util.List;

public class CombStatsCommand implements CommandHandler {
    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    public CombStatsCommand() {

    }

    @Override
    public void execute(Update update) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            String hql = "from User where chatId = :chatId and combSize is not null order by combSize desc";

            Query<Stat>  query = session.createQuery(hql, Stat.class);
            query.setParameter("chatId", update.getMessage().getChatId());
            List<Stat> list = query.list();




            session.getTransaction().commit();



        }
        long chatId = update.getMessage().getChatId();

//        StringBuilder stringBuilder = new StringBuilder();
//        String statsSql = "SELECT user_name, comb_size FROM users WHERE chat_id = ? AND comb_size IS NOT NULL ORDER BY comb_size DESC";
//
//        try (PreparedStatement statsStmt = connection.prepareStatement(statsSql)) {
//            statsStmt.setLong(1, chatId);
//
//            try (ResultSet rs = statsStmt.executeQuery()) {
//                stringBuilder.append(BotMessages.COMB_STATS_HEADER.format());
//                stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
//                stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());
//
//                while (rs.next()) {
//                    String userName = rs.getString("user_name");
//                    int combSize = rs.getInt("comb_size");
//                    stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), userName, combSize));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            bot.sendMessage(chatId, BotMessages.SQL_ERROR.format());
//            return;
//        }
//
//        bot.sendMessage(chatId, stringBuilder.toString());
    }
}
