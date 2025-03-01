package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.ChatApi;
import ru.saveldu.enums.ChatApiType;

@Component

public class SwitchAICommand implements CommandHandler {

    private final MyAmazingBot bot;
    private final AiChatHandler aiChatHandler;
    private final ChatApi gigaChatApi;
    private final ChatApi deepSeekApi;

    @Autowired
    @Lazy
    public SwitchAICommand(MyAmazingBot bot, AiChatHandler aiChatHandler, ChatApi gigaChatApi, ChatApi deepSeekApi) {
        this.bot = bot;
        this.aiChatHandler = aiChatHandler;
        this.gigaChatApi = gigaChatApi;
        this.deepSeekApi = deepSeekApi;
    }

    private static final Logger logger = LoggerFactory.getLogger(SwitchAICommand.class);

    @Override
    public void execute(Update update) {
        if (isUserAdmin(update.getMessage().getFrom().getId())) {
            ChatApiType newApiType = aiChatHandler.switchApi();
            String newApiTypeString = newApiType.toString();
            bot.sendMessage(update.getMessage().getChatId(), "New API: " + newApiTypeString);
            logger.info("Switched API: " + newApiTypeString);
        } else {
            bot.sendMessage(update.getMessage().getChatId(), "You are not an admin.");
        }
    }

    @Override
    public String getName() {
        return "aiswitch";
    }

    private boolean isUserAdmin(long userId) {
        return userId == 128697674;
    }
}
