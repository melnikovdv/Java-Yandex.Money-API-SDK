package ru.yandex.money.api;

import org.junit.Test;

/**
 * Unit tests.
 */
public class ApiTest {
    private static final String CLIENT_ID = "1234567890";

    @Test
    public void TestSimple() {
        YandexMoney ym = new YandexMoneyImpl(CLIENT_ID);
    }

}
