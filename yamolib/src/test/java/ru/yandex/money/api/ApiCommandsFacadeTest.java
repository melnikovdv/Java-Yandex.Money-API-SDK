package ru.yandex.money.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.rights.IdentifierType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

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
        System.out.println(facade.requestPaymentToPhone(AUTH_TOKEN, "79111234567", BigDecimal.valueOf(1.50)));
    }

    @Test
    public void testRequestP2p() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.requestPaymentP2P(AUTH_TOKEN, "onehalf.3544@yandex.ru", IdentifierType.EMAIL,
                BigDecimal.ONE, "comment", "message", "label"));
    }

    @Test
    public void testOperationHistory() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.operationHistory(AUTH_TOKEN, 0, 5, EnumSet.of(OperationHistoryType.deposition), true, null, null, "labeled payment"));
    }

    @Test
    public void testFundraisingStats() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.fundraisingStats(AUTH_TOKEN, "incoming payment"));
    }

    @Test
    public void testOperationHistoryByPeriod() throws InsufficientScopeException, InvalidTokenException, IOException {
        Date from = createDate(2013, 10, 11, 23, 0);
        Date till = createDate(2013, 10, 11, 1, 0);

        System.out.println(facade.operationHistory(AUTH_TOKEN, 0, 5, null, true, from, till, null));
    }

    @Test
    public void testOperationDetails() throws InsufficientScopeException, InvalidTokenException, IOException {
        System.out.println(facade.operationDetail(AUTH_TOKEN, "434813468870011012"));
    }


    @Test
    public void testRfc3339() throws Exception {
        assertEquals("2013-10-11T23:00:00+0400",
                ApiCommandsFacadeImpl.RFC_3339.get().format(createDate(2013, 10, 11, 23, 0)));

        assertEquals("2013-10-11T01:00:00+0400",
                ApiCommandsFacadeImpl.RFC_3339.get().format(createDate(2013, 10, 11,  1, 0)));
    }

    private Date createDate(int year, int month, int day, int hours, int minutes) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month-1);
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.HOUR_OF_DAY, hours);
        date.set(Calendar.MINUTE, minutes);
        date.set(Calendar.SECOND, minutes);
        return date.getTime();
    }
}
