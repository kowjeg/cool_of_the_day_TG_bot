package ru.saveldu.commands;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.ChatApi;
import ru.saveldu.enums.ChatApiType;

@Component
@Slf4j

public class AiChatHandler implements CommandHandler {

    private ChatApi chatApi;
    private final ChatApi gigaChatApi;
    private final ChatApi deepSeekApi;
    private ChatApiType currentApi = ChatApiType.DEEPSEEK;


    private final MyAmazingBot bot;

    @Autowired
    @Lazy
    public AiChatHandler(
            @Qualifier("gigaChatApi") ChatApi gigaChatApi,
            @Qualifier("deepSeekApi") ChatApi deepSeekApi,
            MyAmazingBot bot) {
        this.gigaChatApi = gigaChatApi;
        this.deepSeekApi = deepSeekApi;
        this.bot = bot;
        chatApi = deepSeekApi;
    }

    public ChatApiType switchApi() {
        log.info("Current API type: {}", currentApi);
        if (currentApi == ChatApiType.GIGACHAT) {
            currentApi = ChatApiType.DEEPSEEK;
            chatApi = deepSeekApi;
        } else {
            currentApi = ChatApiType.GIGACHAT;
            chatApi = gigaChatApi;
        }
        log.info("Switched API to: {}", currentApi);
        return currentApi;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            String response = chatApi.sendTextRequest(String.valueOf(chatId), update);
            bot.sendReplyMessage(chatId, response, update.getMessage().getMessageId());
        } catch (Exception e) {
            log.error("Error processing chat request: {}", e.getMessage(), e);
            bot.sendMessage(chatId, "Нет настроения общаться. Весеннее обострение.");
        }
    }

    @Override
    public String getName() {
        return "aichat";
    }
}
