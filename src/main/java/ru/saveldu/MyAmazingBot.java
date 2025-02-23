package ru.saveldu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.ChatApi;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.commands.*;
import ru.saveldu.enums.ChatApiType;

import java.util.HashMap;

public class MyAmazingBot extends MultiSessionTelegramBot {

    private static final Logger logger = LoggerFactory.getLogger(MyAmazingBot.class);

    private static MyAmazingBot instance;
    private static final String TELEGRAM_BOT_NAME = System.getenv("BOT_USERNAME");
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN");

    private AiChatHandler aiChatHandler;
    private SummaryCommandHandler summaryCommandHandler;

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

    public void initializeCommands() throws Exception {
        commands.put("/play", new PlayCombGameCommand());
        commands.put("/register", new RegistrationCommand());
        commands.put("/stats", new ShowStatsCommand());
        commands.put("/cooloftheday", new ChooseCoolOfTheDayCommand());
        commands.put("/topcombs", new CombStatsCommand());
        commands.put("/changepromptds", new ChangePromptDSCommand());
        commands.put("/switchai", new SwitchAICommand());
        commands.put("/summary", new SummaryCommandHandler(new DeepSeekApi()));
        commands.put("/switchsummary", new SummaryCommandSwitcher());
        try {
            aiChatHandler = new AiChatHandler(ChatApiType.DEEPSEEK);
            summaryCommandHandler = new SummaryCommandHandler(new DeepSeekApi());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Message message = update.getMessage();
        String messageText = message.getText();
        Long chatId = message.getChatId();
        boolean isPrivateChat = message.getChat().isUserChat(); // группа или личный чат

        try {
            //если не команда добавляем в историю чата
            if (!messageText.startsWith("/")) {
                summaryCommandHandler.addMessage(update);

                // Если это реплай — обработка через дипсик
                if (message.isReply() && message.getReplyToMessage().getFrom().getUserName().equals(getBotUsername())) {
                    aiChatHandler.execute(update);
                }

                return;
            }


            String[] parts = messageText.split(" ", 2); // ["/play@buzzcatbot"]
            String rawCommand = parts[0]; // "/play@buzzcatbot"
            String[] commandParts = rawCommand.split("@");

            String command = commandParts[0]; // "/play"
            String mentionedBot = (commandParts.length > 1) ? commandParts[1] : null;


            if (!isPrivateChat) {
                if (mentionedBot == null || !mentionedBot.equalsIgnoreCase(getBotUsername())) {
                    return;
                }
            }

            // Проверяем, есть ли такая команда в списке
            CommandHandler handler = commands.get(command);
            if (handler != null) {
                handler.execute(update);
            } else {
                logger.warn("Неизвестная команда: {}", rawCommand);
                sendMessage(chatId, "Неизвестная команда.");
            }

        } catch (Exception e) {
            logger.error("Ошибка обработки сообщения: ", e);
        }
//
    }


}
