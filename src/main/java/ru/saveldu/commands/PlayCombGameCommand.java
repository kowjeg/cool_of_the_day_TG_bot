//package ru.saveldu.commands;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import ru.saveldu.MyAmazingBot;
//import ru.saveldu.entities.User;
//import ru.saveldu.enums.BotMessages;
//import ru.saveldu.repositories.UserRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//
//@Component
//public class PlayCombGameCommand implements CommandHandler {
//
//    private final MyAmazingBot bot;
//
//    private static final int MAX_COMB_CHANGE_SIZE = 14;
//    private static final int MIN_COMB_CHANGE_SIZE = -9;
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    @Lazy
//    public PlayCombGameCommand(MyAmazingBot bot, UserRepository userRepository) {
//        this.bot = bot;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    @Transactional
//    public void execute(Update update) {
//        long chatId = update.getMessage().getChatId();
//        long userId = update.getMessage().getFrom().getId();
//        LocalDate today = LocalDate.now();
//        String userName = update.getMessage().getFrom().getFirstName();
//        String userNameToString = bot.formatUserMention(userName, userId);
//
//        User user = getOrCreateUser(chatId, userId, userName);
//
//        LocalDate lastPlayed = user.getLastPlayedDate();
//        if (lastPlayed != null && lastPlayed.equals(today)) {
//            bot.sendMessage(chatId, BotMessages.ALREADY_PLAYED_COMB.format(userNameToString, user.getCombSize()));
//            return;
//        }
//
//
//        if (user.getCombSize() == null) {
//            user.setCombSize(0);
//        }
//
//
//        Random rand = new Random();
//        int deltaSize = rand.nextInt(MAX_COMB_CHANGE_SIZE - MIN_COMB_CHANGE_SIZE + 1) + MIN_COMB_CHANGE_SIZE;
//        int newCombSize = user.getCombSize() + deltaSize;
//        user.setCombSize(newCombSize);
//        user.setLastPlayedDate(today);
//
//        userRepository.save(user);
//
//        List<User> users = userRepository.findByChatIdOrderByCombSizeDesc(chatId);
//
//        int rank = calculateUserRank(users, userId);
//        sendMessageBasedOnDelta(rank, deltaSize, chatId, userNameToString, newCombSize);
//    }
//
//    private User getOrCreateUser(long chatId, long userId, String userName) {
//        Optional<User> optionalUser = userRepository.findByChatIdAndUserId(chatId, userId);
//        if (optionalUser.isPresent()) {
//            return optionalUser.get();
//        } else {
//            User newUser = new User();
//            newUser.setUserId(userId);
//            newUser.setChatId(chatId);
//            newUser.setUserName(userName);
//            newUser.setCombSize(0);
//            return userRepository.save(newUser);
//        }
//    }
//
//    private static int calculateUserRank(List<User> users, long userId) {
//        int rank = 1;
//        for (User u : users) {
//            if (u.getUserId() == userId) {
//                break;
//            }
//            rank++;
//        }
//        return rank;
//    }
//
//    private void sendMessageBasedOnDelta(int rank, int deltaSize, long chatId, String userNameToString, int newCombSize) {
//        String rankMessage = BotMessages.CURRENT_RANK.format(rank);
//        if (deltaSize > 0) {
//            bot.sendMessage(chatId, BotMessages.INCREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
//        } else if (deltaSize < 0) {
//            bot.sendMessage(chatId, BotMessages.DECREASE_COMB.format(userNameToString, deltaSize, newCombSize) + rankMessage);
//        } else {
//            bot.sendMessage(chatId, BotMessages.NO_CHANGE_COMB.format(userNameToString, newCombSize) + rankMessage);
//        }
//    }
//
//    @Override
//    public String getName() {
//        return "play";
//    }
//}
