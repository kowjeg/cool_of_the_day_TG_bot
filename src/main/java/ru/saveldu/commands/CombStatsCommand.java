package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.enums.BotMessages;

import java.sql.*;

public class CombStatsCommand implements CommandHandler {

    private final Connection connection;
    private final MultiSessionTelegramBot bot;

    public CombStatsCommand(Connection connection, MultiSessionTelegramBot bot) {
        this.connection = connection;
        this.bot = bot;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        StringBuilder stringBuilder = new StringBuilder();
        String statsSql = "SELECT user_name, comb_size FROM users WHERE chat_id = ? AND comb_size IS NOT NULL ORDER BY comb_size DESC";

        try (PreparedStatement statsStmt = connection.prepareStatement(statsSql)) {
            statsStmt.setLong(1, chatId);

            try (ResultSet rs = statsStmt.executeQuery()) {
                stringBuilder.append(BotMessages.COMB_STATS_HEADER.format());
                stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
                stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());

                while (rs.next()) {
                    String userName = rs.getString("user_name");
                    int combSize = rs.getInt("comb_size");
                    stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), userName, combSize));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            bot.sendMessage(chatId, BotMessages.SQL_ERROR.format());
            return;
        }

        bot.sendMessage(chatId, stringBuilder.toString());
    }
}
