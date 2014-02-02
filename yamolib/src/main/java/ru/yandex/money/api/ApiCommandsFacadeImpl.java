package ru.yandex.money.api;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.*;
import ru.yandex.money.api.rights.IdentifierType;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Класс для работы с командами API Яндекс.Деньги. </p>
 * <p>За бесопанстость работы с удаленным сервером овечает Apache HttpClient.
 * Он по умолчанию работает в режиме BrowserCompatHostnameVerifier,
 * этот параметр указывает клиенту, что нужно проверять цепочку сертификатов
 * сервера Янедкс.Денег, с которым мы работаем. При этом дополнительно указывать
 * сертификат Яндекс.Денег не имеет смысла, так как в java встроено доверие
 * ресурсам сертифицированным официальным CA, таким как GTE CyberTrust
 * Solutions, Inc.</p>
 *
 * @author dvmelnikov
 */

public class ApiCommandsFacadeImpl implements ApiCommandsFacade {

    public static final String ACCOUNT_INFO_COMMAND_NAME = "account-info";
    public static final String OPERATION_HISTORY_COMMAND_NAME = "operation-history";
    public static final String OPERATION_DETAILS_COMMAND_NAME = "operation-details";
    public static final String REQUEST_PAYMENT_COMMAND_NAME = "request-payment";
    public static final String PROCESS_PAYMENT_COMMAND_NAME = "process-payment";
    public static final String REVOKE_COMMAND_NAME = "revoke";
    public static final String FUNDRAISING_STATS_COMMAND_NAME = "fundraising-stats";

    private static final ThreadLocal<SimpleDateFormat> RFC_3339 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        }
    };

    private final CommandUrlHolder uri;
    private final YamoneyApiClient yamoneyApiClient;

    /**
     * Создает экземпляр класса.
     *
     * @param client настроенный HttpClient для взаимодействия с сервером Яндекс.Деньги.
     *               Для request-payment и process-payment может понядобиться httpClient
     *               c таймаутом до 60 секунд.
     */
    public ApiCommandsFacadeImpl(HttpClient client) {
        this(client, URI_YM_API);
    }

    /**
     * Создает экземпляр класса.
     *
     * @param client настроенный HttpClient для взаимодействия с сервером Яндекс.Деньги.
     *               Для request-payment и process-payment может понядобиться httpClient
     *               c таймаутом до 60 секунд.
     */
    public ApiCommandsFacadeImpl(HttpClient client, CommandUrlHolder urlHolder) {
        this.yamoneyApiClient = new YamoneyApiClient(client);
        this.uri = urlHolder;
    }

    /**
     * Создает экземпляр класса. Внутри создается httpClient
     * с переданными в параметрах ConnectionManager и HttpParams. Это может
     * быть нужно для нескольких одновременных соединений.
     *
     * @param client             настроенный HttpClient для взаимодействия с сервером Яндекс.Деньги.
     *                           Для request-payment и process-payment может понядобиться httpClient
     *                           c таймаутом до 60 секунд.
     * @param yandexMoneyTestUrl адрес тестововго хоста. Используйте для отладки,
     *                           если у вас есть "эмулятор" Яндекс.Денег
     */
    public ApiCommandsFacadeImpl(HttpClient client, String yandexMoneyTestUrl) {
        this(client, new CommandUrlHolder.ConstantUrlHolder(yandexMoneyTestUrl));
    }

    /**
     * Запрос данных о счете. Баланс, статус идентифицированнности, является ли профессиональным счетом.
     *
     * @param accessToken string токен авторизации пользователя
     * @return Данные счета
     * @throws IOException                При сетевых ошибках
     * @throws InvalidTokenException      Если токен некорректен, или отозван
     * @throws InsufficientScopeException Если для данного токена нет прав на использование account-info
     */
    @Override
    public AccountInfoResponse accountInfo(String accessToken)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return yamoneyApiClient.executeForJsonObjectFunc(uri, ACCOUNT_INFO_COMMAND_NAME, Collections.<NameValuePair>emptyList(), accessToken, AccountInfoResponse.class);
    }

    @Override
    public OperationHistoryResponse operationHistory(String accessToken)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, null, null);
    }

    @Override
    public OperationHistoryResponse operationHistory(String accessToken, Integer startRecord)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, startRecord, null);
    }

    @Override
    public OperationHistoryResponse operationHistory(String accessToken,
                                                     Integer startRecord, Integer records) throws IOException,
            InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, startRecord, records, null);
    }

    @Override
    public OperationHistoryResponse operationHistory(String accessToken, Integer startRecord, Integer records,
                                                     Set<OperationHistoryType> operationsType) throws IOException,
            InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, startRecord, records, operationsType, null, null, null, null);
    }

    @Override
    public OperationHistoryResponse operationHistory(String accessToken,
                                                     Integer startRecord, Integer records,
                                                     Set<OperationHistoryType> operationsType, Boolean fetchDetails,
                                                     Date from, Date till, String label) throws IOException,
            InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        addParamIfNotNull("start_record", startRecord, params);
        addParamIfNotNull("records", records, params);
        addParamIfNotNull("type", joinHistoryTypes(operationsType), params);
        addParamIfNotNull("details", fetchDetails, params);
        addParamIfNotNull("from", from, params);
        addParamIfNotNull("till", till, params);
        addParamIfNotNull("label", label, params);

        return yamoneyApiClient.executeForJsonObjectFunc(uri, OPERATION_HISTORY_COMMAND_NAME, params, accessToken, OperationHistoryResponse.class);
    }

    @Override
    public FundraisingStatsResponse fundraisingStats(String accessToken, String label)
            throws IOException, InvalidTokenException, InsufficientScopeException {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("label", label));
        return yamoneyApiClient.executeForJsonObjectFunc(uri, FUNDRAISING_STATS_COMMAND_NAME, params, accessToken, FundraisingStatsResponse.class);
    }

    @Override
    public OperationDetailResponse operationDetail(String accessToken,
                                                   String operationId) throws IOException, InvalidTokenException,
            InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("operation_id", operationId));

        return yamoneyApiClient.executeForJsonObjectFunc(uri, OPERATION_DETAILS_COMMAND_NAME, params, accessToken, OperationDetailResponse.class);
    }

    @Override
    public RequestPaymentResponse requestPaymentP2P(String accessToken, String to,
                                                    BigDecimal amount, String comment, String message)
            throws IOException, InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("amount", String.valueOf(amount)));
        return requestPaymentP2P(accessToken, to, comment, message, null, params);
    }

    @Override
    public RequestPaymentResponse requestPaymentP2P(String accessToken, String to, IdentifierType identifierType,
                                                    BigDecimal amount, String comment, String message, String label)
            throws IOException, InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("amount", String.valueOf(amount)));
        addParamIfNotNull("identifier_type", identifierType, params);
        return requestPaymentP2P(accessToken, to, comment, message, label, params);
    }

    @Override
    public RequestPaymentResponse requestPaymentP2PDue(String accessToken, String to, IdentifierType identifierType,
                                                       BigDecimal amountDue, String comment, String message,
                                                       String label)
            throws IOException, InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("amount_due", String.valueOf(amountDue)));
        addParamIfNotNull("identifier_type", identifierType, params);
        return requestPaymentP2P(accessToken, to, comment, message, label, params);
    }

    private RequestPaymentResponse requestPaymentP2P(String accessToken, String to,
                                                     String comment, String message, String label,
                                                     List<NameValuePair> params)
            throws IOException, InvalidTokenException, InsufficientScopeException {

        params.add(new BasicNameValuePair("pattern_id", "p2p"));
        params.add(new BasicNameValuePair("to", to));
        addParamIfNotNull("comment", comment, params);
        addParamIfNotNull("message", message, params);
        addParamIfNotNull("label", label, params);

        return yamoneyApiClient.executeForJsonObjectFunc(uri, REQUEST_PAYMENT_COMMAND_NAME, params, accessToken, RequestPaymentResponse.class);
    }

    @Override
    public RequestPaymentResponse requestPaymentToPhone(String accessToken, String phone, BigDecimal amount)
            throws InsufficientScopeException, InvalidTokenException, IOException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("pattern_id", "phone-topup"));
        params.add(new BasicNameValuePair("phone-number", phone));
        params.add(new BasicNameValuePair("amount", String.valueOf(amount)));
        return yamoneyApiClient.executeForJsonObjectFunc(uri, REQUEST_PAYMENT_COMMAND_NAME, params, accessToken, RequestPaymentResponse.class);
    }

    @Override
    public RequestPaymentResponse requestPaymentShop(String accessToken,
                                                     String patternId, Map<String, String> params) throws IOException,
            InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> pars = new ArrayList<NameValuePair>();
        pars.add(new BasicNameValuePair("pattern_id", patternId));
        for (String name : params.keySet()) {
            pars.add(new BasicNameValuePair(name, params.get(name)));
        }

        return yamoneyApiClient.executeForJsonObjectFunc(uri, REQUEST_PAYMENT_COMMAND_NAME, pars, accessToken, RequestPaymentResponse.class);
    }

    @Override
    public ProcessPaymentResponse processPaymentByWallet(String accessToken, String requestId)
            throws IOException, InsufficientScopeException, InvalidTokenException {

        return processPayment(accessToken, requestId, MoneySource.wallet, null);
    }

    @Override
    public ProcessPaymentResponse processPaymentByCard(String accessToken, String requestId, String csc)
            throws IOException, InsufficientScopeException, InvalidTokenException {

        return processPayment(accessToken, requestId, MoneySource.card, csc);
    }

    private ProcessPaymentResponse processPayment(String accessToken,
                                                  String requestId, MoneySource moneySource, String csc)
            throws IOException, InsufficientScopeException,
            InvalidTokenException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("request_id", requestId));
        params.add(new BasicNameValuePair("money_source", moneySource.toString()));
        if (csc != null && (moneySource.equals(MoneySource.card))) {
            params.add(new BasicNameValuePair("csc", csc));
        }
        return yamoneyApiClient.executeForJsonObjectFunc(uri, PROCESS_PAYMENT_COMMAND_NAME, params, accessToken, ProcessPaymentResponse.class);
    }

    @Override
    public void revokeOAuthToken(String accessToken) throws InvalidTokenException, IOException {
        HttpResponse response = null;
        try {
            response = yamoneyApiClient.execPostRequest(new HttpPost(uri.getUrlForCommand(REVOKE_COMMAND_NAME)),
                    accessToken, Collections.<NameValuePair>emptyList());
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_UNAUTHORIZED:
                    throw new InvalidTokenException("invalid token");
                case HttpStatus.SC_BAD_REQUEST:
                    throw new ProtocolRequestException("invalid request");
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    throw new InternalServerErrorException("internal yandex.money server error");
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private void addParamIfNotNull(String paramName, Object value, List<NameValuePair> params) {
        if (value != null) {
            params.add(new BasicNameValuePair(paramName, String.valueOf(value)));
        }
    }

    private void addParamIfNotNull(String paramName, Date date, List<NameValuePair> params) {
        if (date == null) {
            return;
        }
        params.add(new BasicNameValuePair(paramName, formatDate(date)));
    }

    String formatDate(Date date) {
        return RFC_3339.get().format(date).replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
    }

    private String joinHistoryTypes(Set<OperationHistoryType> operationsType) {
        if (operationsType == null || operationsType.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (OperationHistoryType op : operationsType) {
            result.append(op.toString()).append(" ");
        }
        return result.toString().trim();
    }
}
