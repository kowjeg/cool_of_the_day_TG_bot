package ru.saveldu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.models.MessageResponse;
import ru.saveldu.api.models.TextRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeepSeekApi implements ChatApi {
    private final OkHttpClient client;
    private final String apiKey = System.getenv("DEEPSEEK_API_KEY");
    private static String prompt;
    private final long CHAT_SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(15); // время чата с ботом, после таймаута история сообщений очищается
    // дипсик кэш истории работает так - если начало истории нового json совпадает с прошлым 1 в 1 - срабатывает кэш, поэтому перезаписывать первые
    //сообщения будет некорректно, кэш не будет работаьт. ограничения на количество сообщений в сессии общения нет.
    private final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>(); // история сообщений для каждой группы
    private final Map<String, Long> chatTimeout = new ConcurrentHashMap<>(); // мапа с chatId, lastIterationTime

    public static void setPrompt(String prompt) {
        DeepSeekApi.prompt = prompt;
        System.out.println(prompt);
    }

    static {
        try {

            var inputStream = DeepSeekApi.class.getClassLoader().getResourceAsStream("prompt.txt");
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: prompt.txt");
            }

            prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DeepSeekApi() throws Exception {

        this.client = new OkHttpClient.Builder().
                connectTimeout(30, TimeUnit.SECONDS)  // Время ожидания подключения
                .readTimeout(60, TimeUnit.SECONDS)     // Время ожидания ответа
                .writeTimeout(60, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public String sendTextRequest(String groupId, Update update) throws Exception {

        resetHistoryIfTimeout(groupId);
        chatTimeout.put(groupId, System.currentTimeMillis());

        String userMessage = update.getMessage().getText();
        String replyToText = update.getMessage().getReplyToMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String userName = update.getMessage().getFrom().getUserName();

        log.info("Сообщение в [{}] {}: {}", chatId, userName, userMessage);
        Deque<TextRequest.Message> messageHistory = groupMessageHistory.computeIfAbsent(groupId, k -> new LinkedList<>());
        buildMessageHistory(messageHistory, replyToText, userMessage);
        List<TextRequest.Message> fullContext = new ArrayList<>(messageHistory);
        fullContext.add(0, new TextRequest.Message("system", prompt));
        String assistantMessage = apiRequestMethod(fullContext);
        log.info("Сообщение Бота в [{}]: {}", chatId, assistantMessage);

        return assistantMessage;
    }

    public String apiRequestMethod(List<TextRequest.Message> fullContext) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        TextRequest textRequest = new TextRequest("deepseek-chat", false, 0, fullContext);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(textRequest);

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                log.error("response body is null");
                throw new IOException("Empty response from DeepSeek API");
            }

            String jsonResponse = response.body().string();

            if (jsonResponse.isEmpty()) {
                log.error("response body is empty");
                throw new IOException("Empty response from DeepSeek API");
            }

            if (!response.isSuccessful()) {
                log.error("Error api response: {}", response.code());
                throw new RuntimeException("Request failed with code: " + response.code());
            }

            MessageResponse messageResponse = objectMapper.readValue(jsonResponse, MessageResponse.class);

            String assistantMessage = messageResponse.getChoices().get(0).getMessage().getContent();
            return assistantMessage;
        }


    }

    private static void buildMessageHistory(Deque<TextRequest.Message> messageHistory, String replyToText, String userMessage) {

        messageHistory.addLast(new TextRequest.Message("assistant", replyToText));
        messageHistory.addLast(new TextRequest.Message("user", userMessage));
    }

    private void resetHistoryIfTimeout(String groupId) {
        long currentTime = System.currentTimeMillis();
        long lasIterationTime = chatTimeout.getOrDefault(groupId, 0L);

        if (currentTime - lasIterationTime > CHAT_SESSION_TIMEOUT) {
            groupMessageHistory.remove(groupId);
            log.info("История в [{}] очищена, т.к. таймаут был больше {} минут", groupId, TimeUnit.MILLISECONDS.toMinutes(CHAT_SESSION_TIMEOUT));
        }
    }


}