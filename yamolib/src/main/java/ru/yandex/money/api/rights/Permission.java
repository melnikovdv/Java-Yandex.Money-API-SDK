package ru.yandex.money.api.rights;

/**
 * Интерфейс прав на доступ к счету пользователя
 * Реализации см. тут:
 * {@link AccountInfo}, {@link OperationHistory}, {@link OperationDetails},
 * {@link PaymentP2P}, {@link PaymentShop},
 *
 * @author dvmelnikov
 */

public interface Permission {
    /**
     * Метод получения форматированной строки права на доступ
     * @return форматированная строка права на доступ
     */
    String value();
}
