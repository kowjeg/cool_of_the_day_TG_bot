package ru.saveldu.enums;

import java.text.MessageFormat;

public enum BotMessages {
    REGISTER_SUCCESS("{0}, зарегистрировал тебя для выбора фолокрасавчика дня!"),
    COOL_DAY_ALREADY_CHOSEN("Фолокрасавчик дня уже выбран: {0}"),
    NO_REGISTERED_USERS("Нет зарегистрированных участников для выбора фолокрасавчика дня."),
    NO_STATS("За год еще никто ни разу не был фолокрасавчиком"),
    STATS_HEADER("Топ фолокрасавчиков за {0} год:"),
    ANALYZING("Анализирую фолочат за сегодня..."),
    SELECTING("Выбираю лучшие шутки..."),
    CALIBRATING("Калибрую результаты..."),
    COOL_DAY_RESULT("Сегодняшний фолокрасавчик дня: \uD83E\uDEAE {0} \uD83E\uDEAE"),
    UNKNOWN_COMMAND("Я тебя не понимаю");

    private final String template;

    BotMessages(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }
}