package ru.saveldu.services;


import org.springframework.stereotype.Component;

@Component
public interface MessageService {
    void sendMessage(long chatId, String message);
    void sendCoolOfTheDayResult(long chatId, String winnerUserName, long winnerUserId);
    void sendReplyMessage(long chatId, String text, int replyToMessageId);
    String formatUserMention(String usName, long userId);
}
