package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.ChatApiType;
import ru.saveldu.services.MessageService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SwitchAICommand implements CommandHandler {

    private final MessageService messageService;
    private final AiChatHandler aiChatHandler;

    @Override
    public void execute(Update update) {
        if (isUserAdmin(update.getMessage().getFrom().getId())) {
            ChatApiType newApiType = aiChatHandler.switchApi();
            String newApiTypeString = newApiType.toString();
            messageService.sendMessage(update.getMessage().getChatId(), "New API: " + newApiTypeString);
            log.info("Switched API: " + newApiTypeString);
        } else {
            messageService.sendMessage(update.getMessage().getChatId(), "You are not an admin.");
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
