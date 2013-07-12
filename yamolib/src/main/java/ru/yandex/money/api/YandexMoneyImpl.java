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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.*;
import ru.yandex.money.api.rights.AccountInfo;
import ru.yandex.money.api.rights.OperationHistory;
import ru.yandex.money.api.rights.Permission;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

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

public class YandexMoneyImpl implements YandexMoney, Serializable {

    public static final long serialVersionUID = 1L;

    private String clientId;
    private HttpClient client;

    /**
     * Кодировка для url encoding/decoding
     */
    private static String CHARSET = "UTF-8";
    private static String USER_AGENT = "yamolib";

    /**
     * Создает экземпляр класса. Внутри создается httpClient
     * с SingleClientConnManager и таймаутом 60 секунд. Это для случая когда
     * объкту достаточно одного соединения.
     *
     * @param clientId идентификатор приложения в системе Яндекс.Деньги
     */
    public YandexMoneyImpl(final String clientId) {
//        this(clientId, new SingleClientConnManager(null, new SchemeRegistry()), null);
        this(clientId, null);                
    }

    /**
     * Создает экземпляр класса. Внутри создается httpClient
     * с переданными в параметрах ConnectionManager и HttpParams. Это может
     * быть нужно для нескольких одновременных соединений.
     *
     * @param clientId идентификатор приложения в системе Яндекс.Деньги
     */
    public YandexMoneyImpl(final String clientId, HttpClient client) {
        if (clientId == null || (clientId.equals("")))
            throw new IllegalArgumentException("client_id is empty");

        HttpClient httpClient = client;
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
        }                   
        
        this.clientId = clientId;
        this.client = httpClient;
    }

    public String authorizeUri(Collection<Permission> scope,
            String redirectUri, Boolean mobileMode) {

        String sScope = makeScope(scope);
        String authAddress = mobileMode ? URI_YM_AUTH_MOBILE : URI_YM_AUTH;
        try {
            return authAddress + "?client_id=" + clientId +
                    "&response_type=code" +
                    "&scope=" + URLEncoder
                    .encode(sScope, CHARSET) +
                    "&redirect_uri=" + URLEncoder
                    .encode(redirectUri, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("unsupported encoding error", e);
        }
    }

    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
            String redirectUri) throws IOException, InsufficientScopeException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));

        return executeForJsonObjectCommon(YandexMoney.URI_YM_TOKEN, params, null, ReceiveOAuthTokenResponse.class);
    }

    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
            String redirectUri, String clientSecret) throws IOException, InsufficientScopeException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("client_secret", clientSecret));

        return executeForJsonObjectCommon(YandexMoney.URI_YM_TOKEN, params, null, ReceiveOAuthTokenResponse.class);
    }

    public void revokeOAuthToken(String accessToken) throws InvalidTokenException, IOException {
        HttpResponse response = null;

        try {
            response = execPostRequest(YandexMoney.URI_YM_API + "/revoke", null, accessToken);
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
        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/account-info", null, accessToken, AccountInfoResponse.class);
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
        if (startRecord != null)
            params.add(new BasicNameValuePair("start_record",
                    String.valueOf(startRecord)));
        if (records != null)
            params.add(
                    new BasicNameValuePair("records", String.valueOf(records)));
        if (!sType.equals(""))
            params.add(new BasicNameValuePair("type", sType));

        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/operation-history", params, accessToken,
                OperationHistoryResponse.class);
    }

    public OperationDetailResponse operationDetail(String accessToken,
            String operationId) throws IOException, InvalidTokenException,
            InsufficientScopeException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("operation_id", operationId));

        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/operation-details", params, accessToken,
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

        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/request-payment", params, accessToken,
                RequestPaymentResponse.class);
    }

    public RequestPaymentResponse requestPaymentShop(String accessToken,
            String patternId, Map<String, String> params) throws IOException,
            InvalidTokenException, InsufficientScopeException {

        List<NameValuePair> pars = new ArrayList<NameValuePair>();
        pars.add(new BasicNameValuePair("pattern_id", patternId));
        for (String name : params.keySet())
            pars.add(new BasicNameValuePair(name, params.get(name)));

        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/request-payment", pars, accessToken,
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

        return executeForJsonObjectFunc(YandexMoney.URI_YM_API + "/process-payment",
                params, accessToken, ProcessPaymentResponse.class);
    }

    public String getClientId() {
        return clientId;
    }

    private <T> T executeForJsonObjectCommon(String url, List<NameValuePair> params, String accessToken, Class<T> classOfT)
            throws InsufficientScopeException, IOException {

        HttpResponse response = null;

        try {
            response = execPostRequest(url, params, accessToken);
            checkCommonResponse(response);

            return parseJson(response.getEntity(), classOfT);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
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
            post.setEntity(
                    new UrlEncodedFormEntity(params, CHARSET));
        }

        if (accessToken != null)
            post.addHeader("Authorization", "Bearer " + accessToken);

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

    private <T> T parseJson(HttpEntity entity, Class<T> classOfT)
            throws IOException {
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

    private String makeScope(Collection<Permission> scope) {
        if (scope == null) {
            scope = new LinkedList<Permission>();
            scope.add(new AccountInfo());
            scope.add(new OperationHistory());
        }

        StringBuilder sBuilder = new StringBuilder("");
        for (Permission s : scope) {
            sBuilder = sBuilder.append(" ").append(s.value());
        }

        return sBuilder.toString().trim();
    }
}
