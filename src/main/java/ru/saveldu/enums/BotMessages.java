package ru.saveldu.enums;

import java.text.MessageFormat;

public enum BotMessages {
    REGISTER_SUCCESS("{0}, зарегистрировал тебя для участия в конкурсе фолокрасавчиков дня!"),
    ALREADY_REGISTERED("{0}, ты уже зарегистрирован."),
    COOL_DAY_ALREADY_CHOSEN("Фолокрасавчик дня уже выбран: {0}"),
    NO_REGISTERED_USERS("Нет зарегистрированных участников для выбора фолокрасавчика дня."),
    NO_STATS("За год еще никто ни разу не был фолокрасавчиком"),
    STATS_HEADER("Топ фолокрасавчиков за {0} год:"),
    ANALYZING("Анализирую фолочат за сегодня..."),
    SELECTING("Выбираю лучшие шутки..."),
    CALIBRATING("Калибрую результаты..."),
    COOL_DAY_RESULT("{0}, ты фолокрасавчик дня!"),

    ALREADY_PLAYED_COMB("{0}, ты сегодня уже играл, следующая попытка увеличить расческу завтра. Сейчас её размер {1} см."),
    INCREASE_COMB("{0} увеличил твою расческу на {1} см. Теперь её длина {2} см."),
    DECREASE_COMB("{0}, уменьшил твою расчёску на {1} см. Теперь её длина {2} см."),
    NO_CHANGE_COMB("{0}, размер расчёски в этот раз не поменялся, её размер {1} см."),

    UNKNOWN_COMMAND("Я тебя не понимаю");




    private final String template;

    BotMessages(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }
}