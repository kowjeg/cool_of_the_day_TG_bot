package ru.saveldu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.api.models.MessageResponse;
import ru.saveldu.api.models.TextRequest;
import javax.net.ssl.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DeepSeekApi implements ChatApi{
    private final OkHttpClient client;
    private final String apiKey = System.getenv("DEEPSEEK_API_KEY");

    public static void setPrompt(String prompt) {
        DeepSeekApi.prompt = prompt;
        System.out.println(prompt);
    }

    private static  String prompt;
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekApi.class);
    private static final int MAX_HISTORY_LENGTH = 10;

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
    private final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>();

    public DeepSeekApi() throws Exception {
//        TrustManager[] trustAllCertificates = new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return new X509Certificate[0];
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                    }
//                }
//        };
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

        this.client = new OkHttpClient.Builder().
                connectTimeout(30, TimeUnit.SECONDS)  // Время ожидания подключения
                .readTimeout(60, TimeUnit.SECONDS)     // Время ожидания ответа
                .writeTimeout(60, TimeUnit.SECONDS)
//                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCertificates[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public String sendTextRequest(String groupId, Update update) throws Exception {

        String userMessage = update.getMessage().getText();
        String replyToText = update.getMessage().getReplyToMessage().getText();
        logger.info("Сообщение в [{}] {}: {}", update.getMessage().getChatId(), update.getMessage().getFrom().getUserName(),
                update.getMessage().getText());



        Deque<TextRequest.Message> messageHistory = groupMessageHistory.computeIfAbsent(groupId, k -> new LinkedList<>());
        if (messageHistory.isEmpty()) {

            messageHistory.addLast(new TextRequest.Message("assistant", replyToText));
        }
        messageHistory.addLast(new TextRequest.Message("user", userMessage));
        if (messageHistory.size() > MAX_HISTORY_LENGTH) {
            messageHistory.pollFirst();
        }

        List<TextRequest.Message> fullContext = new ArrayList<>(messageHistory);

        fullContext.add(0, new TextRequest.Message("system", prompt));

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

        Response response = client.newCall(request).execute();

        if (response.body() == null) {
            logger.error("response body is null");
            throw new IOException("Empty response from DeepSeek API");
        }

        String jsonResponse = response.body().string();

        if (jsonResponse.isEmpty()) {
            logger.error("response body is empty");
            throw new IOException("Empty response from DeepSeek API");
        }

        if (!response.isSuccessful()) {
            logger.error("Error api response: {}", response.code());
            throw new RuntimeException("Request failed with code: " + response.code());
        }

        MessageResponse messageResponse = objectMapper.readValue(jsonResponse, MessageResponse.class);


        String assistantMessage = messageResponse.getChoices().get(0).getMessage().getContent();
        logger.info("Сообщение Бота в [{}]: {}", update.getMessage().getChatId(), assistantMessage);


        messageHistory.addLast(new TextRequest.Message("assistant", assistantMessage));
        if (messageHistory.size() > MAX_HISTORY_LENGTH) {
            messageHistory.pollFirst();
        }
        return assistantMessage;
    }


}