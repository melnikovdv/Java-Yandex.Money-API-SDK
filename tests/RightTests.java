import org.junit.Assert;
import org.junit.Test;
import ru.yandex.money.api.enums.Destination;
import ru.yandex.money.api.rights.MoneySource;
import ru.yandex.money.api.rights.Payment;
import ru.yandex.money.api.rights.PaymentP2P;

/**
 * @author dvmelnikov
 */

public class RightTests {
    @Test
    public void moneySourceTest() {
        MoneySource right;

        right = new MoneySource(true, true);
        Assert.assertTrue(right.value().equals("money-source(\"wallet\",\"card\")"));

        right = new MoneySource(true, false);
        Assert.assertTrue(right.value().equals("money-source(\"wallet\")"));

        right = new MoneySource(false, true);
        Assert.assertTrue(right.value().equals("money-source(\"card\")"));

        right = new MoneySource(false, false);
        Assert.assertTrue(right.value().equals("money-source(\"wallet\")"));
    }

    @Test
    public void paymentTest() {
        PaymentP2P p2p;
        String s;

        p2p = new PaymentP2P();
        s = p2p.limit(10, "10.50").value();
        Assert.assertTrue(s.equals("payment-p2p.limit(10,10.50)"));

        p2p = new PaymentP2P();
        s = p2p.limit(1, "3000").value();
        Assert.assertTrue(s.equals("payment-p2p.limit(1,3000)"));

        Payment p;

        p = new Payment(Destination.toAccount, "41001901291751", 1, "100");
        System.out.println(p.value());
    }
}
