package ru.yandex.money.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.yandex.money.api.rights.IdentifierType;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Unit tests.
 */
@Ignore("Для ручного тестирования. Нужен токен")
public class ApiCommandsFacadeTest {

    private static final String AUTH_TOKEN = "410011....A3F60A9";

    private static ApiCommandsFacade facade;

    @BeforeClass
    public static void setUpClass() {
        facade = new ApiCommandsFacadeImpl(YandexMoneyImpl.createHttpClient(60000));
    }

    @Test
    public void testAccountInfo() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.accountInfo(AUTH_TOKEN));
    }

    @Test
    public void testRequestPaymentToPhone() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.requestPaymentToPhone(AUTH_TOKEN, "79111234567", "1.50"));
    }

    @Test
    public void testRequestP2p() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.requestPaymentP2P(AUTH_TOKEN, "onehalf.3544@yandex.ru", IdentifierType.EMAIL,
                BigDecimal.ONE, "comment", "message", "label"));
    }

    @Test
    public void testOperationHistory() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.operationHistory(AUTH_TOKEN, 0, 5, null, true, null, null, "labeled payment"));
    }

    @Test
    public void testOperationDetails() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.operationDetail(AUTH_TOKEN, "434813468870011012"));
    }
}
