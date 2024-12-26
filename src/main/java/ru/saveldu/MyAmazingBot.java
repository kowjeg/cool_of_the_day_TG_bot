package ru.saveldu;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.commands.*;
import ru.saveldu.db.DatabaseService;
import ru.saveldu.enums.BotMessages;

import java.sql.*;
import java.util.HashMap;

public class MyAmazingBot extends MultiSessionTelegramBot {

    public static final String TELEGRAM_BOT_NAME = "test";
    public static final String TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN");
    private Connection connection;
    private DatabaseService db;
    private HashMap<String, CommandHandler> commands = new HashMap<>();

    public MyAmazingBot() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
//        db = new DatabaseService();
//        connection = db.getConnection();
        initializeCommands();
    }

    private void initializeCommands() {
        commands.put("/play", new PlayCombGameCommand(connection, this));
        commands.put("/register", new RegistrationCommand(connection, this));
        commands.put("/stats", new ShowStatsCommand(connection, this));
        commands.put("/cooloftheday", new ChooseCoolOfTheDayCommand(connection, this));
        commands.put("/topcombs", new CombStatsCommand(connection,this));
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            long chatId = message.getChatId();

            try {


                String command = messageText.split("[ @]")[0];
                CommandHandler handler = commands.get(command);
                handler.execute(update);
            } catch (Exception e) {
                e.printStackTrace();
//                sendMessage(chatId, BotMessages.UNKNOWN_COMMAND.format());
            }
        }
        String text = loadMessage("main");
        sendTextMessage(text);
    }
}
