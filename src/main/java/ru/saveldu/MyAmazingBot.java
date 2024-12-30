package ru.saveldu;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.commands.*;

import java.util.HashMap;

public class MyAmazingBot extends MultiSessionTelegramBot {

    private static MyAmazingBot instance;
    private static final String TELEGRAM_BOT_NAME = "test";
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN");

    private HashMap<String, CommandHandler> commands = new HashMap<>();

    public static MyAmazingBot getInstance() {
        if (instance == null) {
            instance = new MyAmazingBot();
        }
        return instance;
    }
    private MyAmazingBot() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);

    }

    public void initializeCommands() {
        commands.put("/play", new PlayCombGameCommand());
        commands.put("/register", new RegistrationCommand());
        commands.put("/stats", new ShowStatsCommand());
        commands.put("/cooloftheday", new ChooseCoolOfTheDayCommand());
        commands.put("/topcombs", new CombStatsCommand());
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
