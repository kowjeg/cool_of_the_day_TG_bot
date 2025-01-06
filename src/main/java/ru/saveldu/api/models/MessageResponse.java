package ru.saveldu.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {

    // Здесь мы будем хранить список выборов
    private List<Choice> choices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {

        private Message message;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {

            @JsonProperty("content")
            private String content;
        }
    }
}