package ru.saveldu.commands;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.ChatApi;
import ru.saveldu.enums.ChatApiType;
import ru.saveldu.services.MessageService;

@Component
@Slf4j
public class AiChatHandler implements CommandHandler {

    private final ChatApi gigaChatApi;
    private final ChatApi deepSeekApi;
    private final MessageService messageService;
    private ChatApiType currentApi = ChatApiType.DEEPSEEK;

    @Autowired
    public AiChatHandler(
            @Qualifier("gigaChatApi") ChatApi gigaChatApi,
            @Qualifier("deepSeekApi") ChatApi deepSeekApi,
            MessageService messageService
    ) {
        this.gigaChatApi = gigaChatApi;
        this.deepSeekApi = deepSeekApi;
        this.messageService = messageService;
    }

    public ChatApi getCurrentChatApi() {
        return currentApi == ChatApiType.GIGACHAT ? gigaChatApi : deepSeekApi;
    }

    public ChatApiType switchApi() {
        log.info("Current API type: {}", currentApi);
        ChatApiType newApiType = currentApi == ChatApiType.GIGACHAT ? ChatApiType.DEEPSEEK : ChatApiType.GIGACHAT;
        currentApi = newApiType;
        log.info("Switched API to: {}", currentApi);
        return newApiType;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            String response = getCurrentChatApi().sendTextRequest(String.valueOf(chatId), update);
            messageService.sendReplyMessage(chatId, response, update.getMessage().getMessageId());
        } catch (Exception e) {
            log.error("Error processing chat request: {}", e.getMessage(), e);
            messageService.sendMessage(chatId, "Нет настроения общаться. Весеннее обострение.");
        }
    }

    @Override
    public String getName() {
        return "aichat";
    }
}
