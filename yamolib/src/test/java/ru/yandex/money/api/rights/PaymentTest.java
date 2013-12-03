package ru.yandex.money.api.rights;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.yandex.money.api.rights.IdentifierType.email;
import static ru.yandex.money.api.rights.IdentifierType.phone;

/**
 * <p/>
 * <p/>
 * Created: 25.10.13 22:50
 * <p/>
 *
 * @author OneHalf
 */
public class PaymentTest {

    @Test
    public void testToEmailWithLimit() throws Exception {
        Permission payment = new Payment().toAccount("user@yandex.ru", email).limit(1,"300");
        assertEquals("payment.to-account(\"user@yandex.ru\",\"email\").limit(1,300)", payment.value());
    }

    @Test
    public void testToPhoneWithoutLimit() throws Exception {
        Permission payment = new Payment().toAccount("79219990099", phone);
        assertEquals("payment.to-account(\"79219990099\",\"phone\")", payment.value());
    }

    @Test
    public void testToAccountAsDefaultTypeWithSingleLimit() throws Exception {
        Permission payment = new Payment().toAccount("4100132432532").limit("10");
        assertEquals("payment.to-account(\"4100132432532\").limit(,10)", payment.value());
    }

    @Test
    public void testToPhoneWithLimit() throws Exception {
        Permission payment = new Payment().toPattern("phone-topup").limit(7, "1000");
        assertEquals("payment.to-pattern(\"phone-topup\").limit(7,1000)", payment.value());
    }
}
