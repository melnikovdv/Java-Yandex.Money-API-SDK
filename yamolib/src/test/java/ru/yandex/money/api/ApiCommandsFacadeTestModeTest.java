package ru.yandex.money.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.response.ProcessPaymentResponse;
import ru.yandex.money.api.response.RequestPaymentResponse;
import ru.yandex.money.api.response.util.RequestPaymentError;
import ru.yandex.money.api.rights.IdentifierType;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests.
 */
@Ignore("Для ручного тестирования. Нужен токен")
public class ApiCommandsFacadeTestModeTest {

    private static final String AUTH_TOKEN = "41001.....1A3F60A9";

    private static ApiCommandsFacade facade;
    private static TestUrlHolder urlHolder;

    @BeforeClass
    public static void setUpClass() {
        urlHolder = new TestUrlHolder();
        urlHolder.setTestCard("available");
        facade = new ApiCommandsFacadeImpl(YamoneyApiClient.createHttpClient(60000), urlHolder);
    }

    @Test
    public void testRequestPaymentToPhone() throws InsufficientScopeException, InvalidTokenException, IOException {
        urlHolder.setTestResult(RequestPaymentError.LIMIT_EXCEEDED);
        RequestPaymentResponse request = facade.requestPaymentToPhone(AUTH_TOKEN, "79111234567", BigDecimal.valueOf(1.50));
        assertEquals(RequestPaymentError.LIMIT_EXCEEDED, request.getError());
    }

    @Test
    public void testRequestP2p() throws InsufficientScopeException, InvalidTokenException, IOException {
        urlHolder.setTestResult(RequestPaymentError.AUTHORIZATION_REJECT);
        RequestPaymentResponse requestPaymentResponse = facade.requestPaymentP2P(
                AUTH_TOKEN, "onehalf.3544@yandex.ru", IdentifierType.email, BigDecimal.ONE, "comment", "message", "label");
        assertEquals(RequestPaymentError.AUTHORIZATION_REJECT, requestPaymentResponse.getError());
    }

    @Test
    public void testPaymentToAccount() throws InsufficientScopeException, InvalidTokenException, IOException {
        urlHolder.setTestResult(TestUrlHolder.SUCCESS_CODE);
        RequestPaymentResponse requestPaymentResponse = facade.requestPaymentP2P(
                AUTH_TOKEN, "410011077359617", BigDecimal.ONE, "comment", "message");

        assertTrue(requestPaymentResponse.isSuccess());
        assertEquals("test-p2p", requestPaymentResponse.getRequestId());
        assertTrue(requestPaymentResponse.isTestPayment());
        assertTrue(requestPaymentResponse.isPaymentMethodAvailable(MoneySource.card));

        urlHolder.setTestResult(TestUrlHolder.SUCCESS_CODE);
        ProcessPaymentResponse processPaymentResponse = facade.processPaymentByCard(
                AUTH_TOKEN, requestPaymentResponse.getRequestId(), "000");

        assertTrue(processPaymentResponse.isTestPayment());
        assertTrue(processPaymentResponse.isSuccess());
    }
}
