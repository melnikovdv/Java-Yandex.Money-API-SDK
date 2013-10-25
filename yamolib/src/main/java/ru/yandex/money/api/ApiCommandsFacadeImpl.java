package ru.yandex.money.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Класс для работы с API Яндекс.Деньги. Реализует интерфейс YandexMoney.</p>
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

public class ApiCommandsFacadeImpl implements ApiCommandsFacade, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Кодировка для url encoding/decoding
     */
    private static final String CHARSET = "UTF-8";

    private final HttpClient client;
    private final String uriYamoneyApi;

    /**
     * Создает экземпляр класса.
     * @param client todo
     */
    public ApiCommandsFacadeImpl(HttpClient client) {
        this(client, URI_YM_API);
    }

    /**
     * Создает экземпляр класса. Внутри создается httpClient
     * с переданными в параметрах ConnectionManager и HttpParams. Это может
     * быть нужно для нескольких одновременных соединений.
     * @param client настроенный HttpClient для взаимодействия с сервером Яндекс.Деньги.
     *               Для request-payment и process-payment может понядобиться httpClient
     *               c таймаутом до 60 секунд.
     * @param yandexMoneyTestUrl адрес тестововго хоста. Используйте для отладки,
     *                           если у вас есть "эмулятор" Яндекс.Денег
     */
    public ApiCommandsFacadeImpl(HttpClient client, String yandexMoneyTestUrl) {
        this.client = client;
        this.uriYamoneyApi = yandexMoneyTestUrl;
    }

    public void revokeOAuthToken(String accessToken) throws InvalidTokenException, IOException {
        HttpResponse response = null;

        try {
            response = execPostRequest(uriYamoneyApi + "/revoke", null, accessToken);
            if (response.getStatusLine().getStatusCode() == 401)
                throw new InvalidTokenException("invalid token");

            if (response.getStatusLine().getStatusCode() == 400)
                throw new ProtocolRequestException("invalid request");

            if (response.getStatusLine().getStatusCode() == 500)
                throw new InternalServerErrorException("internal yandex.money server error");
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public AccountInfoResponse accountInfo(String accessToken)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return executeForJsonObjectFunc(uriYamoneyApi + "/account-info", null, accessToken, AccountInfoResponse.class);
    }

    public OperationHistoryResponse operationHistory(String accessToken)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, null, null);
    }

    public OperationHistoryResponse operationHistory(String accessToken, Integer startRecord)
            throws IOException, InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, startRecord, null);
    }

    public OperationHistoryResponse operationHistory(String accessToken,
            Integer startRecord, Integer records) throws IOException,
            InvalidTokenException, InsufficientScopeException {
        return operationHistory(accessToken, startRecord, records, null);
    }

    public OperationHistoryResponse operationHistory(String accessToken,
            Integer startRecord, Integer records,
            Set<OperationHistoryType> operationsType) throws IOException,
            InvalidTokenException, InsufficientScopeException {

        String sType = "";
        if (operationsType != null) {
            for (OperationHistoryType op : operationsType) {
                sType = sType + op.toString() + " ";
            }
            sType = sType.trim();
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (startRecord != null) {
            params.add(new BasicNameValuePair("start_record", String.valueOf(startRecord)));
        }
        if (records != null) {
            params.add(new BasicNameValuePair("records", String.valueOf(records)));
        }
        if (!sType.equals("")) {
            params.add(new BasicNameValuePair("type", sType));
        }
        return executeForJsonObjectFunc(uriYamoneyApi + "/operation-history", params, accessToken,
                OperationHistoryResponse.class);
    }

    public OperationDetailResponse operationDetail(String accessToken,
            String operationId) throws IOException, InvalidTokenException,
            InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("operation_id", operationId));

        return executeForJsonObjectFunc(uriYamoneyApi + "/operation-details", params, accessToken,
                OperationDetailResponse.class);
    }

    public RequestPaymentResponse requestPaymentP2P(String accessToken,
            String to, BigDecimal amount, String comment,
            String message) throws IOException, InvalidTokenException,
            InsufficientScopeException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("pattern_id", "p2p"));
        params.add(new BasicNameValuePair("to", to));
        params.add(new BasicNameValuePair("amount", String.valueOf(amount)));
        params.add(new BasicNameValuePair("comment", comment));
        params.add(new BasicNameValuePair("message", message));

        return executeForJsonObjectFunc(uriYamoneyApi + "/request-payment", params, accessToken,
                RequestPaymentResponse.class);
    }

    public RequestPaymentResponse requestPaymentShop(String accessToken,
            String patternId, Map<String, String> params) throws IOException,
            InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> pars = new ArrayList<NameValuePair>();
        pars.add(new BasicNameValuePair("pattern_id", patternId));
        for (String name : params.keySet())
            pars.add(new BasicNameValuePair(name, params.get(name)));

        return executeForJsonObjectFunc(uriYamoneyApi + "/request-payment", pars, accessToken,
                RequestPaymentResponse.class);
    }

    public ProcessPaymentResponse processPaymentByWallet(String accessToken,
            String requestId) throws IOException, InsufficientScopeException,
            InvalidTokenException {
        return processPayment(accessToken, requestId, MoneySource.wallet, null);
    }

    public ProcessPaymentResponse processPaymentByCard(String accessToken,
            String requestId, String csc)
            throws IOException, InsufficientScopeException,
            InvalidTokenException {
        return processPayment(accessToken, requestId, MoneySource.card, csc);
    }

    private ProcessPaymentResponse processPayment(String accessToken,
            String requestId, MoneySource moneySource, String csc)
            throws IOException, InsufficientScopeException,
            InvalidTokenException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("request_id", requestId));
        params.add(
                new BasicNameValuePair("money_source", moneySource.toString()));
        if (csc != null && (moneySource.equals(MoneySource.card)))
            params.add(new BasicNameValuePair("csc", csc));

        return executeForJsonObjectFunc(uriYamoneyApi + "/process-payment",
                params, accessToken, ProcessPaymentResponse.class);
    }

    private <T> T executeForJsonObjectFunc(String url, List<NameValuePair> params, String accessToken, Class<T> classOfT)
            throws InsufficientScopeException,
            IOException, InvalidTokenException {
        HttpResponse response = null;

        try {
            response = execPostRequest(url, params, accessToken);
            checkFuncResponse(response);

            return parseJson(response.getEntity(), classOfT);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private HttpResponse execPostRequest(String url, List<NameValuePair> params,
            String accessToken) throws IOException {
        HttpPost post = new HttpPost(url);

        if (params != null) {
            post.setEntity(new UrlEncodedFormEntity(params, CHARSET));
        }

        if (accessToken != null) {
            post.addHeader("Authorization", "Bearer " + accessToken);
        }

        try {
            return client.execute(post);
        } catch (IOException e) {
            post.abort();
            throw e;
        }
    }

    private void checkCommonResponse(HttpResponse httpResp) throws
            InternalServerErrorException, InsufficientScopeException {
        int iCode = httpResp.getStatusLine().getStatusCode();

        if (iCode == 400)
            throw new ProtocolRequestException("invalid request");
        if (iCode == 403)
            throw new InsufficientScopeException("insufficient scope");
        if (iCode == 500)
            throw new InternalServerErrorException("internal yandex.money server error");

        if (httpResp.getEntity() == null)
            throw new IllegalStateException("response http entity is empty");
    }

    private void checkFuncResponse(HttpResponse httpResp) throws
            InvalidTokenException, InsufficientScopeException,
            InternalServerErrorException {
        if (httpResp.getStatusLine().getStatusCode() == 401)
            throw new InvalidTokenException("invalid token");
        checkCommonResponse(httpResp);
    }

    private <T> T parseJson(HttpEntity entity, Class<T> classOfT) throws IOException {
        InputStream is = entity.getContent();

        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(
                    FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            return gson.fromJson(
                    new InputStreamReader(is, CHARSET),
                    classOfT);
        } catch (JsonParseException e) {
            throw new IllegalStateException("response decoding failed", e);
        }
    }
}
