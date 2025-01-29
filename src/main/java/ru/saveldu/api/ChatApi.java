package ru.saveldu.api;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ChatApi {
    String sendTextRequest(String groupId, Update update) throws Exception;
}