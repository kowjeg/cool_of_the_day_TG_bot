package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.UserRepository;
import ru.saveldu.services.MessageService;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CombStatsCommand implements CommandHandler {
    private final MessageService messageService;
    private static final int MAX_LENGTH_USERNAME = 14;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        StringBuilder stringBuilder = new StringBuilder();

        //top 10 request
        List<User> combSizesList = userRepository.findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(chatId);

        if (combSizesList.isEmpty()) {
            return;
        }
        stringBuilder.append("`\nТоп 10 расчёсок:\n\n");
        stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
        stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());

        for (User u : combSizesList) {

            String userNameFormatted = truncateUserName(u.getUserName());
            stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), userNameFormatted, u.getCombSize()));
        }
        stringBuilder.append("`");
        messageService.sendMessage(chatId, stringBuilder.toString());
    }

    @NotNull
    private String truncateUserName(String userName) {
        if (userName.length() > MAX_LENGTH_USERNAME) {
            return userName.substring(0, MAX_LENGTH_USERNAME) + "...";
        }
        return userName;
    }

    public String getName() {
        return "topcombs";
    }
}
