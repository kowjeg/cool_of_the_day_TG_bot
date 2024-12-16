package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;

import java.sql.*;
import java.time.LocalDate;

public class ShowStatsCommand implements CommandHandler{

    private final Connection connection;
    private final MultiSessionTelegramBot bot;

    public ShowStatsCommand(Connection connection, MultiSessionTelegramBot bot) {
        this.connection = connection;
        this.bot = bot;
    }
    @Override
    public void execute(Update update) throws SQLException {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        long chatId = update.getMessage().getChatId();

        String statsSql = "SELECT user_name, count FROM stats WHERE chat_id = ? AND year = ? ORDER BY count DESC";
        try (PreparedStatement statsStmt = connection.prepareStatement(statsSql)) {
            statsStmt.setLong(1, chatId);
            statsStmt.setInt(2, currentYear);
            ResultSet rs = statsStmt.executeQuery();

            if (rs.next()) {
                StringBuilder statsMessage = new StringBuilder(BotMessages.STATS_HEADER.format(String.valueOf(currentYear))).append("\n");
                String partCountSql = "SELECT count(*) FROM users";
                Statement partCountStmt = connection.createStatement();
                ResultSet rsCountSet = partCountStmt.executeQuery(partCountSql);

                rsCountSet.next();
                int participants = rsCountSet.getInt(1);
                do {
                    String userName = rs.getString("user_name");
                    int count = rs.getInt("count");
                    statsMessage.append(userName).append(" - ").append(count).append(" раз\n");
                } while (rs.next());
                statsMessage.append("\nВсего фолофанов: " + participants);
                bot.sendMessage(chatId, statsMessage.toString());
            } else {
                bot.sendMessage(chatId, BotMessages.NO_STATS.format());
            }
        }
    }
}
