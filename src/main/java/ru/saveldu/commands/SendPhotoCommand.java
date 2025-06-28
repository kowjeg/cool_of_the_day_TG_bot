package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.services.PhotoSendService;
import ru.saveldu.services.MessageService;

@Component
@RequiredArgsConstructor
public class SendPhotoCommand implements CommandHandler {

    private final PhotoSendService photoSendService;


    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String imageKey = "cat.jpg";

        photoSendService.sendPhoto(chatId, imageKey);
    }

    @Override
    public String getName() {
        return "sendphoto";
    }
}
