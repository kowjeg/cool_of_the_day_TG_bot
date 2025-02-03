package ru.saveldu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.ChatApi;
import ru.saveldu.commands.*;
import ru.saveldu.enums.ChatApiType;

import java.util.HashMap;

public class MyAmazingBot extends MultiSessionTelegramBot {

    private static final Logger logger = LoggerFactory.getLogger(MyAmazingBot.class);

    private static MyAmazingBot instance;
    private static final String TELEGRAM_BOT_NAME = System.getenv("BOT_USERNAME");
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN");

    private AiChatHandler aiChatHandler;

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
//        aiChatHandler = new AiChatHandler(ChatApiType.GIGACHAT);  loop-error

    }

    public void initializeCommands() {
        commands.put("/play", new PlayCombGameCommand());
        commands.put("/register", new RegistrationCommand());
        commands.put("/stats", new ShowStatsCommand());
        commands.put("/cooloftheday", new ChooseCoolOfTheDayCommand());
        commands.put("/topcombs", new CombStatsCommand());
        commands.put("/changepromptds", new ChangePromptDSCommand());
        commands.put("/switchai", new SwitchAICommand());
        try {
            aiChatHandler = new AiChatHandler(ChatApiType.DEEPSEEK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();


            try {
                if (message.isReply() && message.getReplyToMessage().getFrom().getUserName().equals(getBotUsername())) {
                    aiChatHandler.execute(update);
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
