package ru.saveldu;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.saveldu.db.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            Thread.sleep(4000);
            HibernateUtil.getSessionFactory();




            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            MyAmazingBot bot = MyAmazingBot.getInstance();
            telegramBotsApi.registerBot(bot);
            bot.initializeCommands();
            System.out.println("MyAmazingBot successfully started!");

            // Ensure this prcess wait forever
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}