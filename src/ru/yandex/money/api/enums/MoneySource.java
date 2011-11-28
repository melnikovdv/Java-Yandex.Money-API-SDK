package ru.yandex.money.api.enums;

/**
 * Перечисление типа проведения платежа. Принимает значение wallet при оплате с
 * кошелька и card при оплате с привязанной карты
 * @author dvmelnikov
 */

public enum MoneySource {
    /**
     * Кошелек счета Яндекс.Денег
     */
    wallet,

    /**
     * Привязанная банковская карта
     */
    card
}
