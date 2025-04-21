package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.services.MessageService;
import ru.saveldu.services.RegistrationService;


@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationCommand implements CommandHandler {

    private final MessageService messageService;
    private final RegistrationService registrationService;
    @Override
    @Transactional
    public void execute(Update update) {
        if (update.getMessage() == null || update.getMessage().getFrom() == null) {
            log.warn("Не удалось зарегистрировать пользователя: update.getMessage() или getFrom() == null");
            messageService.sendMessage(update.getMessage().getChatId(), "Не удалось зарегистрировать тебя.");
            return;
        }
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getFirstName();

        String userNameToString = messageService.formatUserMention(userName, userId);
        if (registrationService.isUserAlreadyRegistered(userId, chatId)) {
            messageService.sendMessage(chatId, BotMessages.ALREADY_REGISTERED.format(userNameToString));
            return;
        }
        registrationService.registerUser(userId, chatId, userName);
                messageService.sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
    }

    @Override
    public String getName() {
        return "register";
    }
}
