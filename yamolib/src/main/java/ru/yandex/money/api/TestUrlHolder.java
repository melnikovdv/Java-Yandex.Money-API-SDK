package ru.yandex.money.api;

import ru.yandex.money.api.response.util.PaymentErrorCode;

/**
 * Объект для формирования url для тестовых запросов.
 * К адресу добавляются GET-параметры, которые определяют, какой ответ должен вернуть сервер Яндекс.Денег
 * <p/>
 * <p/>
 * Created: 26.10.13 11:41
 * <p/>
 *
 * @author OneHalf
 */
public class TestUrlHolder implements CommandUrlHolder {

    public static final PaymentErrorCode SUCCESS_CODE = new PaymentErrorCode() {
        @Override
        public String getCode() {
            return "success";
        }
    };

    private final String url;

    private volatile boolean testPayment = true;
    private volatile String testCard = null;
    private volatile PaymentErrorCode testResult = null;

    public TestUrlHolder() {
        this(ApiCommandsFacade.URI_YM_API);
    }

    public TestUrlHolder(String url) {
        this.url = url;
    }

    @Override
    public String getUrlForCommand(String commandName) {
        return url + '/' + commandName + "?test_payment=" + testPayment + createSuffix();
    }

    public boolean isTestPayment() {
        return testPayment;
    }

    /**
     *
     * @param testPayment true, если платеж должен быть тестовым
     */
    public void setTestPayment(boolean testPayment) {
        this.testPayment = testPayment;
    }

    public String getTestCard() {
        return testCard;
    }

    /**
     * Отдавать ли признак наличия привязанной к счету карты
     */
    public void setTestCard(String testCard) {
        this.testCard = testCard;
    }

    public PaymentErrorCode getTestResult() {
        return testResult;
    }

    /**
     * @param testResult Код ошибки, которую должен вернуть метод Яндекс.Денег
     *                   Чтобы возвращался успех, установите "success" или null
     */
    public void setTestResult(PaymentErrorCode testResult) {
        this.testResult = testResult;
    }

    private String createSuffix() {
        StringBuilder stringBuilder = new StringBuilder();
        if (testCard != null) {
            stringBuilder.append("&test_card=").append(testCard);
        }
        if (testResult != null) {
            stringBuilder.append("&test_result=").append(testResult.getCode());
        }
        return stringBuilder.toString();
    }
}
