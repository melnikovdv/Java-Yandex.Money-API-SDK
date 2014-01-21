package ru.yandex.money.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.response.util.Operation;
import ru.yandex.money.api.response.util.OperationHistoryError;
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

    private static final String AUTH_TOKEN = "410011077359617.E64E40E29C059741C8E11CA862CA2DE1B4F1CFF331CA7098CA07F4279AA385EE947DC1C60357BFE41F1170AA41B60D8B358A31B037F1ECB2982DA6D319D3B3DC041D6B5791766C1A46C1754BDC817F16CB4EC03B38D0FB1E24874322507E559472DE7E042F9E1851E426FE7A4607A412AA893ACA6B9B95E122538C69AB814705";

    private static ApiCommandsFacade facade;

    @BeforeClass
    public static void setUpClass() {
        facade = new ApiCommandsFacadeImpl(YamoneyApiClient.createHttpClient(60000));
    }

    @Test
    public void testAccountInfo() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.accountInfo(AUTH_TOKEN);
    }

    @Test
    public void testRequestPaymentToPhone() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.requestPaymentToPhone(AUTH_TOKEN, "79111234567", BigDecimal.valueOf(1.50));
    }

    @Test
    public void testRequestP2p() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.requestPaymentP2P(AUTH_TOKEN, "onehalf.3544@yandex.ru", IdentifierType.email,
                BigDecimal.ONE, "comment", "message", "label");
    }

    @Test
    public void testOperationHistory() throws InsufficientScopeException, InvalidTokenException, IOException {
        OperationHistoryResponse response = facade.operationHistory(
                AUTH_TOKEN, 0, 5, EnumSet.of(OperationHistoryType.payment), true, null, null, null);

        for (Operation operation : response.getOperations()) {
            System.out.println(operation);
        }
    }

    @Test
    public void testLabeledOperationHistory() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.operationHistory(AUTH_TOKEN, 0, 5, EnumSet.of(OperationHistoryType.deposition), true, null, null, "labeled payment");
    }

    @Test
    public void testFundraisingStats() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.fundraisingStats(AUTH_TOKEN, "incoming payment");
    }

    @Test
    public void testOperationHistoryByPeriod() throws InsufficientScopeException, InvalidTokenException, IOException {
        Date from = createDate(2013, 10, 11, 23, 0);
        Date till = createDate(2013, 10, 11, 1, 0);

        OperationHistoryResponse response = facade.operationHistory(AUTH_TOKEN, 0, 5, null, true, from, till, null);
        assertEquals(OperationHistoryError.illegal_param_till, response.getError());
    }

    @Test
    public void testOperationDetails() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.operationDetail(AUTH_TOKEN, "434813468870011012");
    }

    @Test @Ignore
    public void testRevoke() throws InsufficientScopeException, InvalidTokenException, IOException {
        facade.revokeOAuthToken(AUTH_TOKEN);
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
