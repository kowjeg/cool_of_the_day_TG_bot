package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class ChooseCoolOfTheDayCommand implements CommandHandler{

    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    public ChooseCoolOfTheDayCommand() {

    }
    @Override
    public void execute(Update update) throws SQLException {

        LocalDate today = LocalDate.now();
        long chatId = update.getMessage().getChatId();

//        String checkSql = "SELECT user_name FROM cool_of_the_day WHERE chat_id = ? AND date = ?";
//        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
//            checkStmt.setLong(1, chatId);
//            checkStmt.setDate(2, Date.valueOf(today));
//            ResultSet rs = checkStmt.executeQuery();
//            if (rs.next()) {
//                bot.sendMessage(chatId, BotMessages.COOL_DAY_ALREADY_CHOSEN.format(rs.getString("user_name")));
//                return;
//            }
//        }

//        String selectSql = "SELECT user_id, user_name FROM users WHERE chat_id = ?";
//        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
//            selectStmt.setLong(1, chatId);
//            ResultSet rs = selectStmt.executeQuery();
//
//            Random random = new Random();
//            if (rs.next()) {
//                rs.last();
//                int rowCount = rs.getRow();
//                int randomRow = random.nextInt(rowCount) + 1;
//                rs.absolute(randomRow);
//
//                long userId = rs.getLong("user_id");
//                String userName = rs.getString("user_name");
//
//                String insertSql = "INSERT INTO cool_of_the_day (chat_id, user_id, user_name, date) VALUES (?, ?, ?, ?)";
//                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
//                    insertStmt.setLong(1, chatId);
//                    insertStmt.setLong(2, userId);
//                    insertStmt.setString(3, userName);
//                    insertStmt.setDate(4, Date.valueOf(today));
//                    insertStmt.executeUpdate();
//                }
//
//                String updateStatsSql = "INSERT INTO stats (chat_id, user_id, user_name, year, count) " +
//                        "VALUES (?, ?, ?, ?, 1) " +
//                        "ON DUPLICATE KEY UPDATE count = count + 1";
//                try (PreparedStatement statsStmt = connection.prepareStatement(updateStatsSql)) {
//                    statsStmt.setLong(1, chatId);
//                    statsStmt.setLong(2, userId);
//                    statsStmt.setString(3, userName);
//                    statsStmt.setInt(4, today.getYear());
//                    statsStmt.executeUpdate();
//                }
//
//                bot.sendMessage(chatId, BotMessages.ANALYZING.format());
//                Thread.sleep(700);
//                bot.sendMessage(chatId, BotMessages.SELECTING.format());
//                Thread.sleep(700);
//                bot.sendMessage(chatId, BotMessages.CALIBRATING.format());
//                Thread.sleep(700);
//                String message = BotMessages.COOL_DAY_RESULT.format(bot.formatUserMention(userName, userId));
//                bot.sendMessage(chatId, message);
//            } else {
//                bot.sendMessage(chatId, BotMessages.NO_REGISTERED_USERS.format());
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}
