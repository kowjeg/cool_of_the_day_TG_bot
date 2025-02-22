package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.MultiSessionTelegramBot;
import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.ChatApi;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.api.models.TextRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SummaryCommandHandler implements CommandHandler{

    private static final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>();
    private final MultiSessionTelegramBot bot = MyAmazingBot.getInstance();

    private String prompt = "Сделай краткое саммари чата, опиши кто что делал:";

    private DeepSeekApi chatApi;

    public void addMessage(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String userName = update.getMessage().getFrom().getFirstName();
        String newMessage =  userName + ": " + update.getMessage().getText();

        groupMessageHistory.computeIfAbsent(chatId, k -> new LinkedList<>());
        groupMessageHistory.get(chatId).addLast(new TextRequest.Message("user", newMessage));

    }


    public SummaryCommandHandler(DeepSeekApi chatApi) {
        this.chatApi = chatApi;
    }

    @Override
    public void execute(Update update) throws SQLException, IOException {
        String groupId = update.getMessage().getChatId().toString();
        String[] getSize = update.getMessage().getText().split(" ");
        if (getSize.length < 2) {
            bot.sendMessage(update.getMessage().getChatId(), "Ошибка: укажите количество сообщений для анализа!");
            return;
        }
        Integer getSizeInteger;
        try {
            getSizeInteger = Integer.parseInt(getSize[1]);
        } catch (NumberFormatException e) {
            bot.sendMessage(update.getMessage().getChatId(), "Ошибка: введите корректное число!");
            return;
        }

        Deque<TextRequest.Message> messageHistory = groupMessageHistory.get(groupId);
        List<TextRequest.Message> fullContext = new ArrayList<>(messageHistory);

        fullContext.add(0, new TextRequest.Message("system", prompt));




        String answer = chatApi.apiRequestMethod(fullContext);


        bot.sendMessage(update.getMessage().getChatId(), answer);



    }
}
