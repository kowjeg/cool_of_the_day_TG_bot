package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.services.MessageService;

import java.sql.SQLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangePromptDSCommand implements CommandHandler{

    private final MessageService messageService;
    @Override
    public void execute(Update update) throws SQLException {
        if (update.getMessage().getFrom().getId()==128697674) {
            DeepSeekApi.setPrompt(update.getMessage().getText());
            messageService.sendMessage(update.getMessage().getChatId(),"Prompt updated!");
            log.info("changed DeepSeek prompt. new prompt:{}", update.getMessage().getText());
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
