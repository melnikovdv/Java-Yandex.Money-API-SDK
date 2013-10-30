package ru.yandex.money.api;

import org.junit.Test;
import ru.yandex.money.api.rights.MoneySource;
import ru.yandex.money.api.rights.Payment;
import ru.yandex.money.api.rights.PaymentP2P;

import static org.junit.Assert.assertEquals;

/**
 * @author dvmelnikov
 */

public class RightTest {
    @Test
    public void moneySourceTest() {
        assertEquals("money-source(\"wallet\",\"card\")", new MoneySource(true, true).value());
        assertEquals("money-source(\"wallet\")", new MoneySource(true, false).value());
        assertEquals("money-source(\"card\")", new MoneySource(false, true).value());
    }

    @Test
    public void paymentTest() {
        assertEquals("payment-p2p.limit(10,10.50)", new PaymentP2P().limit(10, "10.50").value());
        assertEquals("payment-p2p.limit(1,3000)", new PaymentP2P().limit(1, "3000").value());
        assertEquals("payment.to-account(\"41001901291751\").limit(1,100)", new Payment().toAccount("41001901291751").limit(1, "100").value());
    }
}
