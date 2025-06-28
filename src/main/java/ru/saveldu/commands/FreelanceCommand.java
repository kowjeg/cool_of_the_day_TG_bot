package ru.saveldu.commands;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.services.MessageService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class FreelanceCommand implements CommandHandler{

    private final MessageService messageService;

    @Override
    public void execute(Update update) throws IOException, SQLException {
        long chatId = update.getMessage().getChatId();

        messageService.sendMessage(chatId,getItJourneyMessage());

    }


    public static String getItJourneyMessage() {
        LocalDate startDate = LocalDate.of(2019, 11, 18);
        LocalDate currentDate = LocalDate.now();

        LocalDate tempDate = startDate;

        long years = ChronoUnit.YEARS.between(tempDate, currentDate);
        tempDate = tempDate.plusYears(years);

        long months = ChronoUnit.MONTHS.between(tempDate, currentDate);
        tempDate = tempDate.plusMonths(months);

        long days = ChronoUnit.DAYS.between(tempDate, currentDate);

        StringJoiner joiner = new StringJoiner(", ");

        if (years > 0) {
            joiner.add(years + " " + getRussianNumeralForm(years, "год", "года", "лет"));
        }

        if (months > 0) {
            joiner.add(months + " " + getRussianNumeralForm(months, "месяц", "месяца", "месяцев"));
        }

        if (days > 0) {
            joiner.add(days + " " + getRussianNumeralForm(days, "день", "дня", "дней"));
        }

        String result = formatWithAnd(joiner);

        return BotMessages.ALREADY_IN_IT.format(result);
    }
    private static String formatWithAnd(StringJoiner joiner) {
        String[] parts = joiner.toString().split(", ");
        if (parts.length == 1) return parts[0];
        if (parts.length == 2) return parts[0] + " и " + parts[1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                if (i == parts.length - 1) {
                    sb.append(" и ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    private static String getRussianNumeralForm(long number, String one, String few, String many) {
        int n = (int) (number % 100);
        if (n >= 11 && n <= 14) return many;
        switch (n % 10) {
            case 1: return one;
            case 2:
            case 3:
            case 4: return few;
            default: return many;
        }
    }

    @Override
    public String getName() {
        return "freelance";
    }
}
