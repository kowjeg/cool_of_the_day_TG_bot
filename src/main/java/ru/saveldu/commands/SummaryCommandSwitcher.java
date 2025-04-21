package ru.saveldu.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.saveldu.services.MessageService;



@Component
@RequiredArgsConstructor
@Slf4j
public class SummaryCommandSwitcher implements CommandHandler{

    private final MessageService messageService;

    public void execute(Update update)  {
        if(isUserAdmin(update.getMessage().getFrom().getId())) {
            if (SummaryCommandHandler.isActive()) {
                SummaryCommandHandler.setIsActive(false);
            } else {
                SummaryCommandHandler.setIsActive(true);
            }
            messageService.sendMessage(update.getMessage().getChatId(), "Суммаризация переключена, текущий статус: " + SummaryCommandHandler.isActive());
            log.info("Суммаризация переключена, текущий статус: " + SummaryCommandHandler.isActive());

        } else {
            messageService.sendMessage(update.getMessage().getChatId(), "You are not admin");
        }
    }

    @Override
    public String getName() {
        return "summaryswitch";
    }

    private boolean isUserAdmin(long usedId) {
        return usedId == 128697674;
    }
}
