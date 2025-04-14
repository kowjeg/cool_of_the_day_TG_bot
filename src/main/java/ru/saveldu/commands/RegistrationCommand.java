package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.UserRepository;
import ru.saveldu.services.MessageService;

import java.sql.SQLException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationCommand implements CommandHandler {

    private final MessageService messageService;
    private final UserRepository userRepository;

    private boolean isUserAlreadyRegistered(long userId, long chatId) {
        Optional<User> user = userRepository.findByChatIdAndUserId(chatId, userId);
        return user.isPresent();
    }

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
        if (isUserAlreadyRegistered(userId, chatId)) {
            messageService.sendMessage(chatId, BotMessages.ALREADY_REGISTERED.format(userNameToString));
            return;
        }

        User newUser = createUser(userId, chatId, userName);
        userRepository.save(newUser);

        messageService.sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
    }

    private static User createUser(long userId, long chatId, String userName) {
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setChatId(chatId);
        newUser.setUserName(userName);
        return newUser;
    }

    @Override
    public String getName() {
        return "register";
    }
}
