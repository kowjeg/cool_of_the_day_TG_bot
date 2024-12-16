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
    private final int MAX_COMB_CHANGE_SIZE = 14;
    private final int MIN_COMB_CHANGE_SIZE = -9;

    public PlayCombGameCommand(Connection connection, MultiSessionTelegramBot bot) {
        this.connection = connection;
        this.bot = bot;
    }
    @Override
    public void execute(Update update) throws SQLException {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        LocalDate today = LocalDate.now();
        String userName = update.getMessage().getFrom().getUserName();

        String checkSql = "SELECT comb_size, last_played_date FROM users WHERE chat_id = ? AND user_id = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkSql);
        checkStatement.setLong(1, chatId);
        checkStatement.setLong(2, userId);
        ResultSet resultSet = checkStatement.executeQuery();
        if (!resultSet.next()) {
            String registerSql = "INSERT INTO users (chat_id, user_id, user_name, comb_size, last_played_date) " +
                    "VALUES (?, ?, ?, 0, NULL)";
            try (PreparedStatement registerStmt = connection.prepareStatement(registerSql)) {
                registerStmt.setLong(1, chatId);
                registerStmt.setLong(2, userId);
                registerStmt.setString(3, userName);
                registerStmt.executeUpdate();
            }
            execute(update); //
            return;
        } else {
            LocalDate lastPlayed = resultSet.getDate("last_played_date") != null ? resultSet.getDate("last_played_date").toLocalDate() : null;
            if (lastPlayed != null && lastPlayed.equals(today)) {

                PreparedStatement combSizeStatement = connection.prepareStatement("select comb_size from users where user_id = ?");
                combSizeStatement.setLong(1, userId);
                ResultSet resultSetCombSize = combSizeStatement.executeQuery();
                resultSetCombSize.next();
                bot.sendMessage(chatId, bot.formatUserMention(userName, userId) + ", ты сегодня уже играл, следующая попытка увеличить расческу завтра. Сейчас её размер "
                        + resultSetCombSize.getInt(1) + " см");
                return;
            }
            int currentCombSize = resultSet.getInt("comb_size");
            Random rand = new Random();
            int deltaSize = rand.nextInt(MAX_COMB_CHANGE_SIZE - MIN_COMB_CHANGE_SIZE + 1) + MIN_COMB_CHANGE_SIZE;
            int newCombSize = currentCombSize + deltaSize;

            String updateSql = "UPDATE users SET comb_size = ?, last_played_date = ? WHERE chat_id = ? AND user_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {

                updateStmt.setLong(1, newCombSize);
                updateStmt.setDate(2, Date.valueOf(today));
                updateStmt.setLong(3, chatId);
                updateStmt.setLong(4, userId);
                updateStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(deltaSize>0) {
                bot.sendMessage(chatId, bot.formatUserMention(userName, userId) + " увеличил твою расческу на " + deltaSize + "см. Длина твоей расчески " + newCombSize+" см");
            } else if (deltaSize<0) {
                bot.sendMessage(chatId, bot.formatUserMention(userName, userId) + " уменьшил твою расческу на " + -deltaSize + "см. Длина твоей расчески " + newCombSize+" см");

            } else {
                bot.sendMessage(chatId, bot.formatUserMention(userName, userId) + " расческа не изменилась, её размер " + newCombSize+ " см");

            }
        }
    }
}
