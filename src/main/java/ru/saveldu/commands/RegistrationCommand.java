package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationCommand implements CommandHandler{

    private final Connection connection;
    private final MultiSessionTelegramBot bot;

    public RegistrationCommand(Connection connection, MultiSessionTelegramBot bot) {
        this.connection = connection;
        this.bot = bot;
    }
    @Override
    public void execute(Update update) throws SQLException {

        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getUserName();
        String sqlCheckAlreadyRegistered = "SELECT user_id FROM users WHERE user_id = ? and chat_id = ?";
        try (PreparedStatement checkAlreadyRegisteredStatement = connection.prepareStatement(sqlCheckAlreadyRegistered);) {
            checkAlreadyRegisteredStatement.setLong(1, userId);
            checkAlreadyRegisteredStatement.setLong(2, chatId);
            ResultSet rs = checkAlreadyRegisteredStatement.executeQuery();
            if (rs.next()) {
                bot.sendMessage(chatId, bot.formatUserMention(userName, userId) + ", ты уже зарегистрирован");
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        String sql = "INSERT INTO users (chat_id, user_id, user_name) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE user_name = VALUES(user_name)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            stmt.setLong(2, userId);
            stmt.setString(3, userName);
            stmt.executeUpdate();
            bot.sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
        }
    }
}
