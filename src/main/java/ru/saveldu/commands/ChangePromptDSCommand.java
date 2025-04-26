package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.services.MessageService;

import java.sql.SQLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangePromptDSCommand implements CommandHandler{

    private final MessageService messageService;
    @Override
    public void execute(Update update) {

        Long chatId = update.getMessage().getChatId();
        User user = update.getMessage().getFrom();

        if (user.getId()==128697674) {
            String newPrompt = update.getMessage().getText();
            DeepSeekApi.setPrompt(newPrompt);
            messageService.sendMessage(chatId,"Prompt updated!");
            log.info("changed DeepSeek prompt. new prompt:{}", newPrompt);
        }
        else {
            String message = "not admin";
            messageService.sendMessage(update.getMessage().getChatId(),message);
        }
    }
    @Override
    public String getName() {
        return "prompt";
    }
}
