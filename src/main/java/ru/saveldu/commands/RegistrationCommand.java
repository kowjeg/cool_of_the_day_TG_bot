//package ru.saveldu.commands;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//import ru.saveldu.MyAmazingBot;
//
//import ru.saveldu.entities.User;
//import ru.saveldu.enums.BotMessages;
//import ru.saveldu.repositories.UserRepository;
//
//import java.sql.SQLException;
//import java.util.Optional;
//
//@Component
//public class RegistrationCommand implements CommandHandler {
//
//    private final MyAmazingBot bot;
//    private final UserRepository userRepository;
//
//    @Autowired
//    @Lazy
//    public RegistrationCommand(MyAmazingBot bot, UserRepository userRepository) {
//        this.bot = bot;
//        this.userRepository = userRepository;
//    }
//
//    private boolean isUserAlreadyRegistered(long userId, long chatId) {
//        Optional<User> user = userRepository.findByChatIdAndUserId(chatId, userId);
//        return user.isPresent();
//    }
//
//    @Override
//    @Transactional
//    public void execute(Update update) throws SQLException {
//        long chatId = update.getMessage().getChatId();
//        long userId = update.getMessage().getFrom().getId();
//        String userName = update.getMessage().getFrom().getFirstName();
//
//        String userNameToString = bot.formatUserMention(userName, userId);
//        if (isUserAlreadyRegistered(userId, chatId)) {
//            bot.sendMessage(chatId, BotMessages.ALREADY_REGISTERED.format(userNameToString));
//            return;
//        }
//
//        User newUser = new User();
//        newUser.setUserId(userId);
//        newUser.setChatId(chatId);
//        newUser.setUserName(userName);
//        userRepository.save(newUser);
//
//        bot.sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
//    }
//
//    @Override
//    public String getName() {
//        return "register";
//    }
//}
