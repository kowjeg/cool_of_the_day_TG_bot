package ru.saveldu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.hibernate.id.UUIDGenerator;
import ru.saveldu.api.models.AccessTokenResponse;
import ru.saveldu.api.models.MessageResponse;
import ru.saveldu.api.models.TextRequest;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

public class GigaChatApi {
    private final OkHttpClient client;
    private static String accessToken;
    private long lastTimeGetBearerKey;
    private final String apiKey = System.getenv("GIGACHAT_API_KEY");

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

    public String sendTextRequest(String message) throws Exception {

        String accessToken = getAccessToken();

        MediaType mediaType = MediaType.parse("application/json");

        TextRequest textRequest = new TextRequest("GigaChat", false, 0,
                List.of(new TextRequest.Message("system","Отвечай как Александр Фоломкин"),
                        new TextRequest.Message("user",message)));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(textRequest);

        System.out.println(jsonBody);

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Request-ID", "79e41a5f-f180-4c7a-b2d9-393086ae20a1")
                .addHeader("X-Session-ID", "b6874da0-bf06-410b-a150-fd5f9164a0b2")
                .addHeader("X-Client-ID", "b6874da0-bf06-410b-a150-fd5f9164a0b2")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        Response response = client.newCall(request).execute();

        String jsonResponse = response.body().string();

        ObjectMapper mapper = new ObjectMapper();

        MessageResponse messageResponse = mapper.readValue(jsonResponse, MessageResponse.class);


        String res = messageResponse.getChoices().get(0).getMessage().getContent();
        return res;
    }

    public String getAccessToken() throws Exception {
        // if last access key was get < 3600 * 450  millis - return current access key
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
