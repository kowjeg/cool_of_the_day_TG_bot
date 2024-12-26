package ru.saveldu;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.saveldu.db.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            HibernateUtil.getSessionFactory();


            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new MyAmazingBot());
            System.out.println("MyAmazingBot successfully started!");
            // Ensure this prcess wait forever
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}