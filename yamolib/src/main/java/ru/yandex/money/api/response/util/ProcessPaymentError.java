package ru.yandex.money.api.response.util;

/**
 * Date: 18.11.13 20:38
 *
 * @author sergeev
 */
public enum ProcessPaymentError {

    /**
     * Недостаточно средств на карте.
     */
    not_enough_funds,

    /**
     * Невозможно провести платеж с карты. Возможно требуется миграция на Skrat.
     * */
    money_source_not_available,

    /**
     * Невозможно провести платеж с карты. Возможно требуется 3DSecure авторизация.
     */
    authorization_reject,

    /**
     * Превышен лимит платежа за период.
     */
    limit_exceeded,

    /**
     * Протух контракт платежа или контракт не найден в БД (протух контекст)
     */
    contract_not_found,

    /**
     * Аккаунт заблокирован. Нужно отправить пользователя по url из поля "account_unblock_uri"
     */
    account_blocked
}
