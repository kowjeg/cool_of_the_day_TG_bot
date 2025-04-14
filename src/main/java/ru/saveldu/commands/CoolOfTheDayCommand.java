package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.saveldu.entities.User;
import ru.saveldu.exceptions.COTDAlreadyChosen;
import ru.saveldu.exceptions.NoUserInChat;
import ru.saveldu.services.CoolOfTheDayService;
import ru.saveldu.services.MessageService;
import ru.saveldu.services.MessageServiceImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoolOfTheDayCommand implements CommandHandler {

    private final CoolOfTheDayService coolOfTheDayService;
    private final MessageService messageService;

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        try {
            User winner = coolOfTheDayService.chooseCoolOfTheDay(chatId);
            messageService.sendCoolOfTheDayResult(chatId, winner.getUserName(), winner.getUserId());
        } catch (COTDAlreadyChosen e) {
            messageService.sendMessage(chatId, e.getMessage());
        } catch (NoUserInChat e) {
            messageService.sendMessage(chatId, e.getMessage());
        }
        catch (Exception e) {
            log.error("Error in ChooseCoolOfTheDayCommand", e);
            messageService.sendMessage(chatId, "Произошла ошибка. Попробуйте позже.");
        }
    }

    @Override
    public String getName() {
        return "cooloftheday";
    }
}
