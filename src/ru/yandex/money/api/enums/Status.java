package ru.yandex.money.api.enums;

/**
 * Статус операции. Используется в response-объектах запросов.
 * @author dvmelnikov
 */

public enum Status {

    /**
     * Успех
     */
    success,

    /**
     * Отклонено
     */
    refused,

    /**
     * В процессе выполнения
     */
    in_progress
}
