package ru.saveldu.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;



@Data
public class TextRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("stream")
    private boolean stream;

    @JsonProperty("update_interval")
    private int updateInterval;

    @JsonProperty("messages")
    private List<Message> messages;


    public TextRequest(String model, boolean stream, int updateInterval, List<Message> messages) {
        this.model = model;
        this.stream = stream;
        this.updateInterval = updateInterval;
        this.messages = messages;
    }


    @Data
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;



        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

    }
}