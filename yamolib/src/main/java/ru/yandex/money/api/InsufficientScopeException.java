package ru.yandex.money.api;

/**
 * Запрошена операция, на которую у токена нет прав.
 * @author dvmelnikov
 */

public class InsufficientScopeException extends Exception {

    InsufficientScopeException(String message) {
        super(message);
    }
}
