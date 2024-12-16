package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class PlayCombGameCommand implements CommandHandler{

    private final Connection connection;
    private final MultiSessionTelegramBot bot;

    public PlayCombGameCommand(Connection connection, MultiSessionTelegramBot bot) {
        this.connection = connection;
        this.bot = bot;
    }
    @Override
    public void execute(Update update) throws SQLException {
//        long chatId = update.getMessage().getChatId();
//        long userId = update.getMessage().getFrom().getId();
//        LocalDate today = LocalDate.now();
//        String userName = update.getMessage().getFrom().getUserName();
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
//                PreparedStatement combSizeStatement = connection.prepareStatement("select comb_size from users where user_id = ?");
//                combSizeStatement.setLong(1, userId);
//                ResultSet resultSetCombSize = combSizeStatement.executeQuery();
//                resultSetCombSize.next();
//                bot.sendMessage(chatId, ", ты сегодня уже играл, следующая попытка увеличить расческу завтра. Сейчас её размер: "
//                        + resultSetCombSize.getInt(1));
//                return;
//            }
//            int currentCombSize = resultSet.getInt("comb_size");
//            Random rand = new Random();
//            int newCombSize = currentCombSize + (rand.nextInt(11) - 6);
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
//            bot.sendMessage(chatId, "длина твоей расчески: " + newCombSize);
//
//        }
    }
}
