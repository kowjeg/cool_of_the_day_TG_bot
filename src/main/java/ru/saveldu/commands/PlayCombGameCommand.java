package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class PlayCombGameCommand implements CommandHandler{


//    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    private final int MAX_COMB_CHANGE_SIZE = 14;
    private final int MIN_COMB_CHANGE_SIZE = -9;

    public PlayCombGameCommand() {

    }

    @Override
    public void execute(Update update) throws SQLException {
//        long chatId = update.getMessage().getChatId();
//        long userId = update.getMessage().getFrom().getId();
//        LocalDate today = LocalDate.now();
//        String userName = update.getMessage().getFrom().getFirstName();
//        String userNameToString = bot.formatUserMention(userName, userId);
//
//        String checkSql = "SELECT comb_size, last_played_date FROM users WHERE chat_id = ? AND user_id = ?";
//        PreparedStatement checkStatement = connection.prepareStatement(checkSql);
//        checkStatement.setLong(1, chatId);
//        checkStatement.setLong(2, userId);
//        ResultSet resultSet = checkStatement.executeQuery();
//        if (!resultSet.next()) {
//            String registerSql = "INSERT INTO users (chat_id, user_id, user_name, comb_size, last_played_date) " +
//                    "VALUES (?, ?, ?, 0, NULL)";
//            try (PreparedStatement registerStmt = connection.prepareStatement(registerSql)) {
//                registerStmt.setLong(1, chatId);
//                registerStmt.setLong(2, userId);
//                registerStmt.setString(3, userName);
//                registerStmt.executeUpdate();
//            }
//            execute(update); //
//            return;
//        } else {
//            LocalDate lastPlayed = resultSet.getDate("last_played_date") != null ? resultSet.getDate("last_played_date").toLocalDate() : null;
//            if (lastPlayed != null && lastPlayed.equals(today)) {
//
//                PreparedStatement combSizeStatement = connection.prepareStatement("select comb_size from users where user_id = ? AND chat_id = ?");
//                combSizeStatement.setLong(1, userId);
//                combSizeStatement.setLong(2,chatId);
//                ResultSet resultSetCombSize = combSizeStatement.executeQuery();
//                resultSetCombSize.next();
//                int combSize = resultSetCombSize.getInt(1);
//                bot.sendMessage(chatId, BotMessages.ALREADY_PLAYED_COMB.format(userNameToString,
//                        combSize));
//                return;
//            }
//            int currentCombSize = resultSet.getInt("comb_size");
//            Random rand = new Random();
//            int deltaSize = rand.nextInt(MAX_COMB_CHANGE_SIZE - MIN_COMB_CHANGE_SIZE + 1) + MIN_COMB_CHANGE_SIZE;
//            int newCombSize = currentCombSize + deltaSize;
//
//            String updateSql = "UPDATE users SET comb_size = ?, last_played_date = ? WHERE chat_id = ? AND user_id = ?";
//            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
//
//                updateStmt.setLong(1, newCombSize);
//                updateStmt.setDate(2, Date.valueOf(today));
//                updateStmt.setLong(3, chatId);
//                updateStmt.setLong(4, userId);
//                updateStmt.executeUpdate();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//            if(deltaSize>0) {
//                bot.sendMessage(chatId, BotMessages.INCREASE_COMB.format(userNameToString, deltaSize, newCombSize));
//            } else if (deltaSize<0) {
//                bot.sendMessage(chatId, BotMessages.DECREASE_COMB.format(userNameToString, deltaSize, newCombSize));
//            } else {
//                bot.sendMessage(chatId, BotMessages.NO_CHANGE_COMB.format(userNameToString, newCombSize));
//            }

    }
}
