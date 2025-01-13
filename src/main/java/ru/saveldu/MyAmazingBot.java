package ru.saveldu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.commands.*;

import java.util.HashMap;

public class MyAmazingBot extends MultiSessionTelegramBot {

    private static final Logger logger = LoggerFactory.getLogger(MyAmazingBot.class);

    private static MyAmazingBot instance;
    private static final String TELEGRAM_BOT_NAME = System.getenv("BOT_USERNAME");
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN");

    private static GigaChatHandler gigaChatHandler = null;

    private HashMap<String, CommandHandler> commands = new HashMap<>();

    public static MyAmazingBot getInstance() {
        if (instance == null) {
            try {
                logger.info("bot starts");
                instance = new MyAmazingBot();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }
    private MyAmazingBot() throws Exception {
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
                if(message.isReply() && message.getReplyToMessage().getFrom().getUserName().equals(getBotUsername())) {
                    if (gigaChatHandler == null) {
                        gigaChatHandler = new GigaChatHandler();
                    }
                    gigaChatHandler.execute(update);
                    return;
                }

                String command = messageText.split("[ @]")[0];
                CommandHandler handler = commands.get(command);
                handler.execute(update);
            } catch (Exception e) {
                e.printStackTrace();
//                sendMessage(chatId, BotMessages.UNKNOWN_COMMAND.format());
            }
        }
//
    }
}
