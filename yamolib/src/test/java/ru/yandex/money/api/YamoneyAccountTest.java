package ru.yandex.money.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * <p/>
 * <p/>
 * Created: 05.12.13 9:47
 * <p/>
 *
 * @author OneHalf
 */
public class YamoneyAccountTest {

    /**
     * Контрольные примеры
     */
    @Test
    public void testCorrectAccounts() {
        // контрольные примеры счетов
        new YamoneyAccount("512345678925");
        new YamoneyAccount("498765432131");
        new YamoneyAccount("41001100113");
        new YamoneyAccount("41001123494");
        new YamoneyAccount("41002100117");
        new YamoneyAccount("41002123498");
        new YamoneyAccount("41003100121");
        new YamoneyAccount("41003123403");
    }

    /**
     * негативный тест - ошибка в одной цифре счета
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectAccount() {
        new YamoneyAccount("41003103403");
    }

    /**
     * негативный тест - ошибка в одной цифре счета
     */
    @Test
    public void testIncorrectAccounts() {
        assertFalse(YamoneyAccount.isValidAccountNumber("41003103403"));
        assertFalse(YamoneyAccount.isValidAccountNumber("410jhk3403"));
        assertFalse(YamoneyAccount.isValidAccountNumber("4106546535"));
    }
}
