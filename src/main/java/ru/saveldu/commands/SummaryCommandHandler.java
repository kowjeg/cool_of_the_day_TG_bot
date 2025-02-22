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
        String newMessage = userName + ": " + update.getMessage().getText();

        groupMessageHistory.computeIfAbsent(chatId, k -> new LinkedList<>());
        Deque<TextRequest.Message> messageQueue = groupMessageHistory.get(chatId);


        messageQueue.addLast(new TextRequest.Message("user", newMessage));


        if (messageQueue.size() > 1000) {
            messageQueue.removeFirst();
        }
    }


    public SummaryCommandHandler(DeepSeekApi chatApi) {
        this.chatApi = chatApi;
    }


    @Override
    public void execute(Update update) throws SQLException, IOException {
        String groupId = update.getMessage().getChatId().toString();
        String[] getSize = update.getMessage().getText().split(" ");
        int getSizeInteger = 100;

        if (getSize.length >= 2) {
            try {
                getSizeInteger = Integer.parseInt(getSize[1]);
            } catch (NumberFormatException e) {
                bot.sendMessage(update.getMessage().getChatId(), "Ошибка: введите корректное число!");
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


        String responseMessage = "Суммаризация по " + messagesCount + " сообщениям:\n" + answer;

        bot.sendMessage(update.getMessage().getChatId(), responseMessage);
    }
}
