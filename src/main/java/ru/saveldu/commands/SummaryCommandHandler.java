package ru.saveldu.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.MyAmazingBot;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.api.models.TextRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component

public class SummaryCommandHandler implements CommandHandler{
    private static boolean isActive;

    private static final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>();
    private final MyAmazingBot bot;
    private static final Logger logger = LoggerFactory.getLogger(SummaryCommandHandler.class);

    private String prompt = "Сделай краткое саммари чата, опиши кто что делал:";

    private DeepSeekApi chatApi;

    @Autowired
    @Lazy
    public SummaryCommandHandler(DeepSeekApi chatApi, MyAmazingBot bot) {
        this.bot = bot;
        this.chatApi = chatApi;
        isActive = true;
    }


    public static boolean isActive() {
        return isActive;
    }

    public static void setIsActive(boolean isActive) {
        SummaryCommandHandler.isActive = isActive;
    }

    public void addMessage(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String userName = update.getMessage().getFrom().getFirstName();
        String newMessage = userName + ": " + update.getMessage().getText();

        groupMessageHistory.computeIfAbsent(chatId, k -> new LinkedList<>());
        Deque<TextRequest.Message> messageQueue = groupMessageHistory.get(chatId);


        messageQueue.addLast(new TextRequest.Message("user", newMessage));


        if (messageQueue.size() > 1000) {
            messageQueue.removeFirst();
        }
    }





    @Override
    public void execute(Update update) throws SQLException, IOException {
        if(!isActive) {
            return;
        }
        String groupId = update.getMessage().getChatId().toString();
        String[] getSize = update.getMessage().getText().split(" ");
        int getSizeInteger = 100;

        if (getSize.length >= 2) {
            try {
                getSizeInteger = Integer.parseInt(getSize[1]);
            } catch (NumberFormatException e) {
                bot.sendMessage(update.getMessage().getChatId(), "Ошибка: введите корректное число!");
                logger.info("Неправильно обработана команда, возможно было передано не число вторым параметром");
                return;
            }
        }
        if (getSizeInteger > 1000 || getSizeInteger < 1) {
            bot.sendMessage(update.getMessage().getChatId(), "Суммаризация может быть по минимум 1 сообщению, максимум по 1000");
            return;
        }

        Deque<TextRequest.Message> messageHistory = groupMessageHistory.getOrDefault(groupId, new LinkedList<>());
        int actualHistorySize = messageHistory.size();


        int messagesCount = Math.min(getSizeInteger, actualHistorySize);

        List<TextRequest.Message> fullContext = new ArrayList<>(messageHistory);
        int fromIndex = Math.max(fullContext.size() - messagesCount, 0);
        List<TextRequest.Message> lastMessages = fullContext.subList(fromIndex, fullContext.size());


        List<TextRequest.Message> contextWithPrompt = new ArrayList<>();
        contextWithPrompt.add(new TextRequest.Message("system", prompt));
        contextWithPrompt.addAll(lastMessages);

        String answer = chatApi.apiRequestMethod(contextWithPrompt);
        logger.info("Суммаризация по " + messagesCount + " сообщениям в " + update.getMessage().getChat().getFirstName());


        String responseMessage = "Суммаризация по " + messagesCount + " сообщениям:\n" + answer;

        bot.sendMessage(update.getMessage().getChatId(), responseMessage);
    }

    @Override
    public String getName() {
        return "summary";
    }
}
