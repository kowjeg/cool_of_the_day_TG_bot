package ru.saveldu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.entities.CoolOfTheDay;
import ru.saveldu.entities.Stat;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.repositories.CoolOfTheDayRepository;
import ru.saveldu.repositories.StatRepository;
import ru.saveldu.repositories.UserRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class ChooseCoolOfTheDayCommand implements CommandHandler {


    private final MyAmazingBot bot;
    private final CoolOfTheDayRepository coolOfTheDayRepository;
    private final StatRepository statRepository;
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    public ChooseCoolOfTheDayCommand(MyAmazingBot bot, CoolOfTheDayRepository coolOfTheDayRepository,
                                     StatRepository statRepository, UserRepository userRepository) {
        this.coolOfTheDayRepository = coolOfTheDayRepository;
        this.statRepository = statRepository;
        this.userRepository = userRepository;
        this.bot = bot;
    }

    @Override
    @Transactional
    public void execute(Update update) throws SQLException {

        LocalDate today = LocalDate.now();
        long chatId = update.getMessage().getChatId();

        Optional<CoolOfTheDay> existingCoolOfTheDay = coolOfTheDayRepository.findByChatIdAndDate(chatId, today);
        if (existingCoolOfTheDay.isPresent()) {
            bot.sendMessage(chatId, BotMessages.COOL_DAY_ALREADY_CHOSEN.format(existingCoolOfTheDay.get().getUserName()));
            return;
        }

        List<User> usersInChat = userRepository.getUsersByChatId(chatId);
        if (usersInChat.isEmpty()) {
            return;
        }
        Random random = new Random();
        int participants = usersInChat.size();
        int winnerNumber = random.nextInt(participants);
        User winner = usersInChat.get(winnerNumber);

        //insert to cool_of_the_day

        CoolOfTheDay coolOfTheDay = new CoolOfTheDay();
        coolOfTheDay.setDate(today);
        coolOfTheDay.setUserId(winner.getUserId());
        coolOfTheDay.setChatId(winner.getChatId());
        coolOfTheDay.setUserName(winner.getUserName());
        coolOfTheDayRepository.save(coolOfTheDay);

        //insert to stats
        Optional<Stat> existingStatLine = statRepository.findByChatIdAndUserIdAndYear(chatId, winner.getUserId(), today.getYear());

        if (existingStatLine.isPresent()) {
            Stat stat = existingStatLine.get();
            stat.setCountWins(stat.getCountWins() + 1);
            statRepository.save(stat);
        } else {
            Stat stat = new Stat();
            stat.setYear(today.getYear());
            stat.setUserId(winner.getUserId());
            stat.setUserName(winner.getUserName());
            stat.setChatId(winner.getChatId());
            stat.setCountWins(1);
            statRepository.save(stat);
        }


        try {
            bot.sendMessage(chatId, BotMessages.ANALYZING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.SELECTING.format());
            Thread.sleep(700);
            bot.sendMessage(chatId, BotMessages.CALIBRATING.format());
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    @Override
    public String getName() {
        return "cooloftheday";
    }
}
