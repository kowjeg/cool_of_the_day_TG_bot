package ru.saveldu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saveldu.entities.CoolOfTheDay;
import ru.saveldu.entities.Stat;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.exceptions.COTDAlreadyChosen;
import ru.saveldu.exceptions.FoloBotException;
import ru.saveldu.exceptions.NoUserInChat;
import ru.saveldu.repositories.CoolOfTheDayRepository;
import ru.saveldu.repositories.StatRepository;
import ru.saveldu.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoolOfTheDayService {

    private final CoolOfTheDayRepository coolOfTheDayRepository;
    private final StatRepository statRepository;
    private final UserRepository userRepository;

    @Transactional
    public User chooseCoolOfTheDay(long chatId) {
        LocalDate today = LocalDate.now();

        Optional<CoolOfTheDay> coolOfTheDayOpt = coolOfTheDayRepository.findByChatIdAndDate(chatId, today);
        if (coolOfTheDayOpt.isPresent()) {
            String winnerName = coolOfTheDayOpt.get().getUserName();
            throw new COTDAlreadyChosen(BotMessages.COOL_DAY_ALREADY_CHOSEN.format(winnerName));
        }

        List<User> usersInChat = userRepository.getUsersByChatId(chatId);
        if (usersInChat.isEmpty()) {
            throw new NoUserInChat(BotMessages.NO_USER_IN_CHAT.format());
        }

        Random random = new Random();
        int winnerIndex = random.nextInt(usersInChat.size());
        User winner = usersInChat.get(winnerIndex);

        CoolOfTheDay coolOfTheDay = new CoolOfTheDay();
        coolOfTheDay.setDate(today);
        coolOfTheDay.setUserId(winner.getUserId());
        coolOfTheDay.setChatId(winner.getChatId());
        coolOfTheDay.setUserName(winner.getUserName());
        coolOfTheDayRepository.save(coolOfTheDay);

        updateStats(chatId, winner, today.getYear());

        return winner;
    }

    private void updateStats(long chatId, User winner, int year) {
        Optional<Stat> existingStatLine = statRepository.findByChatIdAndUserIdAndYear(chatId, winner.getUserId(), year);

        if (existingStatLine.isPresent()) {
            log.info("Пользователь {} {} уже есть в БД, добавляю +1 к победе в красавчике дня", existingStatLine.get().getUserId(), existingStatLine.get().getUserName());
            Stat stat = existingStatLine.get();
            stat.setCountWins(stat.getCountWins() + 1);
            statRepository.save(stat);
        } else {
            Stat stat = new Stat();
            stat.setYear(year);
            stat.setUserId(winner.getUserId());
            stat.setUserName(winner.getUserName());
            stat.setChatId(winner.getChatId());
            stat.setCountWins(1);
            statRepository.save(stat);
            log.info("Пользователь {} {}  выиграл первый раз, добавляется новая строка в telegram_bot.stats", stat.getUserName(), stat.getUserName());
        }
    }
}