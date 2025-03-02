package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.UserRepository;

import java.util.List;

@Component
public class CombStatsCommand implements CommandHandler {
    private final MyAmazingBot bot;
    private static final int MAX_LENGTH_USERNAME = 14;
    private static final int TOP_COMB_LIST_SIZE = 10;

    private static final Logger logger = LoggerFactory.getLogger(CombStatsCommand.class);

    private final UserRepository userRepository;

    @Autowired
    @Lazy
    public CombStatsCommand(MyAmazingBot bot, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bot = bot;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        StringBuilder stringBuilder = new StringBuilder();

        List<User> combSizesList = userRepository.findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(chatId);

        if (combSizesList.isEmpty()) {
            return;
        }
        stringBuilder.append("`\nТоп 10 расчёсок:\n\n");
        stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), "Фолофан", "Размер (см)"));
        stringBuilder.append(BotMessages.COMB_STATS_SEPARATOR.format());

        for (User u : combSizesList) {

            StringBuilder userNameActual = new StringBuilder(u.getUserName());
            if (userNameActual.length() > MAX_LENGTH_USERNAME) {
                userNameActual = new StringBuilder(userNameActual.substring(0, MAX_LENGTH_USERNAME)).append("...");

            }
            stringBuilder.append(String.format(BotMessages.COMB_STATS_FORMAT.format(), userNameActual, u.getCombSize()));
        }
        stringBuilder.append("`");
        bot.sendMessage(chatId, stringBuilder.toString());
    }

    public String getName() {
        return "topcombs";
    }
}
