package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.entities.Stat;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.StatRepository;
import ru.saveldu.services.MessageService;


import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowStatsCommand implements CommandHandler {

    private final MessageService messageService;
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void execute(Update update) throws SQLException {
        long chatId = update.getMessage().getChatId();
        int currentYear = LocalDate.now().getYear();

        List<Stat> statsList = statRepository.findByChatIdAndYear(chatId,currentYear);

        StringBuilder statMessage = new StringBuilder(BotMessages.STATS_HEADER.format(String.valueOf(currentYear))).append("\n");
        for (Stat s : statsList) {
            statMessage.append(s.getUserName()).append(" - ").append(s.getCountWins()).append(" раз\n");
        }

        messageService.sendMessage(chatId, statMessage.toString());
    }
    @Override
    public String getName() {
        return "stats";
    }
}

