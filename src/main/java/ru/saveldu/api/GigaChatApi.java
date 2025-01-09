package ru.saveldu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.hibernate.id.UUIDGenerator;
import ru.saveldu.api.models.AccessTokenResponse;
import ru.saveldu.api.models.MessageResponse;
import ru.saveldu.api.models.TextRequest;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GigaChatApi {
    private final OkHttpClient client;
    private static String accessToken;
    private long lastTimeGetBearerKey;
    private final String apiKey = System.getenv("GIGACHAT_API_KEY");
    private static String prompt;

    static{
        try {
            prompt = Files.readString(Path.of("src/main/resources/prompt.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private final Map<String, Deque<TextRequest.Message>> groupMessageHistory = new ConcurrentHashMap<>();

    public GigaChatApi() throws Exception {
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

        this.client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCertificates[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public String sendTextRequest(String groupId, String userMessage) throws Exception {
        String accessToken = getAccessToken();

        Deque<TextRequest.Message> messageHistory = groupMessageHistory.computeIfAbsent(groupId, k -> new LinkedList<>());

        messageHistory.addLast(new TextRequest.Message("user", userMessage));
        if (messageHistory.size() > 2) {
            messageHistory.pollFirst();
        }


        List<TextRequest.Message> fullContext = new ArrayList<>(messageHistory);
        fullContext.add(0, new TextRequest.Message("system", prompt));

        MediaType mediaType = MediaType.parse("application/json");
        TextRequest textRequest = new TextRequest("GigaChat", false, 0, fullContext);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(textRequest);

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Session-ID", "71666d47-135d-4180-a7ca-e4af261d6bf9")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed with code: " + response.code());
        }

        String jsonResponse = response.body().string();
        MessageResponse messageResponse = objectMapper.readValue(jsonResponse, MessageResponse.class);



        String assistantMessage = messageResponse.getChoices().get(0).getMessage().getContent();


        messageHistory.addLast(new TextRequest.Message("assistant", assistantMessage));
        if (messageHistory.size() > 2) {
            messageHistory.pollFirst();
        }

        return assistantMessage;
    }

    public String getAccessToken() throws Exception {
        if (lastTimeGetBearerKey > 0 && (System.currentTimeMillis() - lastTimeGetBearerKey) < 3600 * 450) {
            return accessToken;
        }

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "scope=GIGACHAT_API_PERS");
        String uuid = UUID.randomUUID().toString();
        Request request = new Request.Builder()
                .url("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
                .method("POST", body)
                .addHeader("RqUID", uuid)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed with code: " + response.code());
        }

        String jsonResponse = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        AccessTokenResponse accessTokenResponse = objectMapper.readValue(jsonResponse, AccessTokenResponse.class);

        accessToken = accessTokenResponse.getAccessToken();
        lastTimeGetBearerKey = System.currentTimeMillis();
        return accessToken;
    }
}



