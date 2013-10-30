package ru.yandex.money.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.rights.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests.
 */
public class TokenRequesterTest {

    private static final String CLIENT_ID = "F45C1450AD086173AC2B07B1719103DF70022DC39F7F04636EBA165B37079817";

    private static TokenRequester requester;

    @BeforeClass
    public static void setUpClass() {
        requester = new TokenRequesterImpl(CLIENT_ID, YandexMoneyImpl.createHttpClient(4000));
    }

    @Test
    public void testGetRequestToken() {
        Collection<Permission> permissions = new ArrayList<Permission>();
        permissions.add(new AccountInfo());
        permissions.add(new OperationHistory());
        permissions.add(new OperationDetails());
        permissions.add(new MoneySource(true, true));

        permissions.add(new PaymentP2P().limit(1, "3"));
        permissions.add(new Payment().toPattern("phone-topup").limit(1, "10"));

        String s = requester.authorizeUri(permissions, "http://ya.ru", false);
        String expected = "https://sp-money.yandex.ru/oauth/authorize?client_id=F45C1450AD086173AC2B07B1719103DF70022DC39F7F04636EBA165B37079817" +
                "&response_type=code&scope=account-info+operation-history+operation-details+money-source%28%22wallet%22%2C%22card%22%29+" +
                "payment-p2p.limit%281%2C3%29+payment.to-pattern%28%22phone-topup%22%29.limit%281%2C10%29&redirect_uri=http%3A%2F%2Fya.ru";
        assertEquals(expected, s);
    }

    @Test @Ignore("Не работает автоматически")
    public void testGetAuthToken() throws IOException, InsufficientScopeException {
        String reqToken = "70B006...B46C9E72";
        ReceiveOAuthTokenResponse receiveOAuthTokenResponse = requester.receiveOAuthToken(reqToken, "http://ya.ru");
        System.out.println(receiveOAuthTokenResponse);
        assertTrue(receiveOAuthTokenResponse.isSuccess());
    }
}
