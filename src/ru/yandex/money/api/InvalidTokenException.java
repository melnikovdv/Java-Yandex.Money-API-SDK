package ru.yandex.money.api;

/**
 * Указан несуществующий, просроченный, или отозванный токен.
 * @author dvmelnikov
 */

public class InvalidTokenException extends Exception {

    InvalidTokenException(String message) {
        super(message);
    }
}
