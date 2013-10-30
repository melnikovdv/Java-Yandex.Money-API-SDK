package ru.yandex.money.api.rights;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p/>
 * <p/>
 * Created: 25.10.13 23:38
 * <p/>
 *
 * @author OneHalf
 */
public class MoneySourceTest {

    @Test
    public void testWallet() throws Exception {
        MoneySource moneySource = new MoneySource(true, false);
        assertEquals("money-source(\"wallet\")", moneySource.value());
    }

    @Test
    public void testWalletAndCard() throws Exception {
        MoneySource moneySource = new MoneySource(true, true);
        assertEquals("money-source(\"wallet\",\"card\")", moneySource.value());
    }
}
