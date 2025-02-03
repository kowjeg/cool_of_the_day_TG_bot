package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.ChatApi;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.enums.ChatApiType;

public class AiChatHandler implements CommandHandler{
    private static final Logger logger = LoggerFactory.getLogger(AiChatHandler.class);

    private static ChatApi chatApi;
    private final MultiSessionTelegramBot bot;
    private static ChatApiType currentApi;

    public AiChatHandler(ChatApiType apiType) throws Exception {
        this.bot = MyAmazingBot.getInstance();
        currentApi = apiType;
        this.chatApi = createChatApi(apiType);
    }

    public static ChatApiType switchApi() {
        try {
            logger.info("current api type: " + currentApi);
            if (currentApi == ChatApiType.GIGACHAT) {
                currentApi = ChatApiType.DEEPSEEK;
            } else {
                currentApi = ChatApiType.GIGACHAT;
            }
            chatApi = createChatApi(currentApi);
            logger.info("Switched API to: {}", currentApi);
        } catch (Exception e) {
            logger.error("Error switching API: {}", e.getMessage(), e);
        }
        return currentApi;
    }

    private static ChatApi createChatApi(ChatApiType apiType) throws Exception {
        switch (apiType) {
            case GIGACHAT:
                logger.info("Creating Gigachat API");
                return new ru.saveldu.api.GigaChatApi();
            case DEEPSEEK:
                logger.info("Creating DeepSeek API");
                return new DeepSeekApi();
            default:
                throw new IllegalArgumentException("Unsupported ChatApiType: " + apiType);
        }
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        try {

            String response = chatApi.sendTextRequest(String.valueOf(chatId), update);
            bot.sendReplyMessage(chatId, response, update.getMessage().getMessageId());
        } catch (Exception e) {
            logger.error("Error processing chat request: {}", e.getMessage(), e);
            bot.sendMessage(chatId, "Произошла ошибка. Попробуйте позже.");
        }
    }



}
