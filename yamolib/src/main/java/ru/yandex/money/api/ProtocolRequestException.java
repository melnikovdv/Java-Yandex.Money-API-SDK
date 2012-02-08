package ru.yandex.money.api;

/**
 * Ошибка невалидного запроса по протоколу обмена.
 * Формат HTTP запроса не соответствует протоколу. Запрос невозможно разобрать,
 * либо заголовок Authorization отсутствует, либо имеет некорректное значение.
 * @author dvmelnikov
 */
public class ProtocolRequestException extends RuntimeException {

    private static final long serialVersionUID = 4192097904020534835L;

    ProtocolRequestException(String s) {
        super(s);
    }
}
