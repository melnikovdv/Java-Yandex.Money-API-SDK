package ru.yandex.money.api.enums;

/**
 * Перечисление типов запросов истории операций. Принимает значение приход
 * (deoposition) и расход (payment)
 * @author dvmelnikov
 */

public enum OperationHistoryType {
    /**
     * Тип расход (платежи)
     */
    payment,

    /**
     * Тип приход (пополнения)
     */
    deposition
}
