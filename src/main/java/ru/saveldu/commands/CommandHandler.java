package ru.saveldu.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLException;

public interface CommandHandler {
    void execute(Update update) throws SQLException;
}
