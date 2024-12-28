package ru.saveldu.commands;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.db.HibernateUtil;
import ru.saveldu.entities.User;
import ru.saveldu.enums.BotMessages;
import ru.saveldu.MultiSessionTelegramBot;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationCommand implements CommandHandler{


    private final MultiSessionTelegramBot bot  = MyAmazingBot.getInstance();

    public RegistrationCommand() {

    }
    @Override
    public void execute(Update update) throws SQLException {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hqlCheckAlreadyRegistered = "FROM User where userId = : userId and chatId = : chatId";
            User existedUser = session.createQuery(hqlCheckAlreadyRegistered, User.class)
                    .setParameter("userId", userId)
                    .setParameter("chatId", chatId)
                    .uniqueResult();
            String userName = update.getMessage().getFrom().getFirstName();
            String userNameToString = bot.formatUserMention(userName, userId);
            if (existedUser != null) {
                bot.sendMessage(chatId, BotMessages.ALREADY_REGISTERED.format(userNameToString));
                return;
            }

            Transaction transactionToInsert = session.beginTransaction();
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setChatId(chatId);
            newUser.setUserName(userName);
            session.save(newUser);
            transactionToInsert.commit();
            bot.sendMessage(chatId, BotMessages.REGISTER_SUCCESS.format(userName));
        } catch (Exception e) {
            e.printStackTrace();
            bot.sendMessage(chatId, "Ошибка при регистрации. Попробуйте позже.");
        }
    }
}
