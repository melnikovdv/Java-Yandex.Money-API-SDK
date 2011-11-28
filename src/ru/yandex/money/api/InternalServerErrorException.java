package ru.yandex.money.api;

import java.io.IOException;

/**
 * Внутренняя ошибка сервера Яндекс.Денег. Приложению следует повторить запрос
 * через некоторое время с теми же параметрами.
 * @author dvmelnikov
 */

public class InternalServerErrorException extends IOException {

    InternalServerErrorException(String s) {
        super(s);
    }
}
