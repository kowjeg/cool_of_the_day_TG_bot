package ru.saveldu.commands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.api.models.TextRequest;
import ru.saveldu.services.MessageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SummaryCommandHandler implements CommandHandler {
    private static boolean isActive = true;
    private final int MAX_HISTORY_LENGTH = 1000;
    private final int MIN_HISTORY_LENGTH = 1;

    private static final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>();
    private final MessageService messageService;
    private String prompt = "Сделай краткое саммари чата, опиши кто что делал:";
    private final DeepSeekApi chatApi;

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
        log.info(chatId + ": " + newMessage);

        if (messageQueue.size() > 1000) {
            messageQueue.removeFirst();
        }
    }

    @Override
    public void execute(Update update) throws IOException {
        if (!isActive) {
            return;
        }
        String groupId = update.getMessage().getChatId().toString();
        String[] getSize = update.getMessage().getText().split(" ");
        int getSizeInteger = 100;

        if (getSize.length >= 2) {
            try {
                getSizeInteger = Integer.parseInt(getSize[1]);
            } catch (NumberFormatException e) {
                messageService.sendMessage(update.getMessage().getChatId(), "Ошибка: введите корректное число!");
                log.info("Неправильно обработана команда, возможно было передано не число вторым параметром");
                return;
            }
        }
        if (getSizeInteger > MAX_HISTORY_LENGTH || getSizeInteger < MIN_HISTORY_LENGTH) {
            messageService.sendMessage(update.getMessage().getChatId(), "Суммаризация может быть по минимум " + MIN_HISTORY_LENGTH
                    + " сообщению, максимум по " + MAX_HISTORY_LENGTH);
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
        log.info("Суммаризация по " + messagesCount + " сообщениям в " + update.getMessage().getChat().getUserName());

        String responseMessage = "Суммаризация по " + messagesCount + " сообщениям:\n" + answer;
        messageService.sendMessage(update.getMessage().getChatId(), responseMessage);
    }

    @Override
    public String getName() {
        return "summary";
    }
}
