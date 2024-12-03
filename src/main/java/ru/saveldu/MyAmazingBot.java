package ru.saveldu;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.saveldu.enums.BotMessages;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class MyAmazingBot extends MultiSessionTelegramBot {


    public static final String TELEGRAM_BOT_NAME = "testbot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7116585849:AAElte4B1YxlQi4JJj2AudRDfzU0_cdqnAE"; //TODO: добавь токен бота в кавычках

    private Connection connection;



    public MyAmazingBot() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
        initializeDatabaseConnection();
    }


    private void initializeDatabaseConnection() {
        try {
            Thread.sleep(4000);

            String connectString = "jdbc:mysql://" + System.getenv("HOST_NAME") + ":"
                    +  System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME");
            connection = DriverManager.getConnection(
                    connectString,
                    System.getenv("DB_USER"),
                    System.getenv("DB_PASSWORD")
            );

        } catch (SQLException | InterruptedException e) {
//            System.out.println("test");
            throw new RuntimeException("Ошибка подключения к базе данных", e);
        }
    }

    private void ensureConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                initializeDatabaseConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось восстановить соединение с базой данных", e);
        }
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            long chatId = message.getChatId();
            long userId = message.getFrom().getId();

            try {
                ensureConnection();
                if (messageText.startsWith("/register")) {
                    registerUser(chatId, userId, message.getFrom().getFirstName());
                } else if (messageText.startsWith("/cooloftheday")) {
                    chooseCoolOfTheDay(chatId);
                } else if (messageText.startsWith("/stats")) {
                    showStats(chatId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(chatId, BotMessages.UNKNOWN_COMMAND.format());
            }
        }
        //TODO: основной функционал бота будем писать здесь
        String text = loadMessage("main");
        sendTextMessage(text);

    }

    private void showStats(long chatId) throws SQLException {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        String statsSql = "SELECT user_name, count FROM stats WHERE chat_id = ? AND year = ? ORDER BY count DESC";
        try (PreparedStatement statsStmt = connection.prepareStatement(statsSql)) {
            statsStmt.setLong(1, chatId);
            statsStmt.setInt(2, currentYear);
            ResultSet rs = statsStmt.executeQuery();

            if (rs.next()) {
                StringBuilder statsMessage = new StringBuilder(BotMessages.STATS_HEADER.format(currentYear)).append("\n");

                do {
                    String userName = rs.getString("user_name");
                    int count = rs.getInt("count");
                    statsMessage.append(userName).append(" - ").append(count).append(" раз\n");
                } while (rs.next());

                sendMessage(chatId, statsMessage.toString());
            } else {
                sendMessage(chatId, BotMessages.NO_STATS.format());
            }
        }
    }


    private void registerUser(long chatId, long userId, String userName) throws SQLException {
        String sql = "INSERT INTO users (chat_id, user_id, user_name) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE user_name = VALUES(user_name)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            stmt.setLong(2, userId);
            stmt.setString(3, userName);
            stmt.executeUpdate();
            sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
        }
    }

    private void chooseCoolOfTheDay(long chatId) throws SQLException {
        LocalDate today = LocalDate.now();

        String checkSql = "SELECT user_name FROM cool_of_the_day WHERE chat_id = ? AND date = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setLong(1, chatId);
            checkStmt.setDate(2, Date.valueOf(today));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                sendMessage(chatId, BotMessages.COOL_DAY_ALREADY_CHOSEN.format(rs.getString("user_name")));
                return;
            }
        }

        String selectSql = "SELECT user_id, user_name FROM users WHERE chat_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            selectStmt.setLong(1, chatId);
            ResultSet rs = selectStmt.executeQuery();

            Random random = new Random();
            if (rs.next()) {
                rs.last();
                int rowCount = rs.getRow();
                int randomRow = random.nextInt(rowCount) + 1;
                rs.absolute(randomRow);

                long userId = rs.getLong("user_id");
                String userName = rs.getString("user_name");

                String insertSql = "INSERT INTO cool_of_the_day (chat_id, user_id, user_name, date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, chatId);
                    insertStmt.setLong(2, userId);
                    insertStmt.setString(3, userName);
                    insertStmt.setDate(4, Date.valueOf(today));
                    insertStmt.executeUpdate();
                }

                String updateStatsSql = "INSERT INTO stats (chat_id, user_id, user_name, year, count) " +
                        "VALUES (?, ?, ?, ?, 1) " +
                        "ON DUPLICATE KEY UPDATE count = count + 1";
                try (PreparedStatement statsStmt = connection.prepareStatement(updateStatsSql)) {
                    statsStmt.setLong(1, chatId);
                    statsStmt.setLong(2, userId);
                    statsStmt.setString(3, userName);
                    statsStmt.setInt(4, today.getYear());
                    statsStmt.executeUpdate();
                }

                sendMessage(chatId, BotMessages.ANALYZING.format());
                Thread.sleep(700);
                sendMessage(chatId, BotMessages.SELECTING.format());
                Thread.sleep(700);
                sendMessage(chatId, BotMessages.CALIBRATING.format());
                Thread.sleep(700);
                sendMessage(chatId, BotMessages.COOL_DAY_RESULT.format(userName));
            } else {
                sendMessage(chatId, BotMessages.NO_REGISTERED_USERS.format());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
