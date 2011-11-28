package ru.yandex.money.api.response;

import java.math.BigDecimal;

/**
 * Класс для возврата результата метода accountInfo.
 * Показывает номер счета, текущий баланс и код валюты пользователя
 * (возможные значения кода валюты: 643 - российский рубль).
 * @author dvmelnikov
 */

public class AccountInfoResponse {

    private String account;
    private BigDecimal balance;
    private String currency;

    private AccountInfoResponse() {
    }

    /**
     *
     * @return номер счета пользователя
     */
    public String getAccount() {
        return account;
    }

    /**
     *
     * @return остаток на счете пользователя
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     *
     * @return Код валюты счета пользователя. Всегда 643 (рубль РФ по
     * стандарту ISO 4217).
     */
    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "AccountInfoResponse{" +
                "account='" + account + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                '}';
    }
}
