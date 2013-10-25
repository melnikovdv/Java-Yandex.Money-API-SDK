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
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.rights.AccountInfo;
import ru.yandex.money.api.rights.OperationHistory;
import ru.yandex.money.api.rights.Permission;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Класс для получения токена для API Яндекс.Деньги. Реализует интерфейс TokenRequester.</p>
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
public class TokenRequesterImpl implements TokenRequester, Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private HttpClient client;

    /**
     * Кодировка для url encoding/decoding
     */
    private static final String CHARSET = "UTF-8";

    /**
     * Создает экземпляр класса.
     *
     * @param clientId идентификатор приложения в системе Яндекс.Деньги
     * @param client todo
     */
    public TokenRequesterImpl(final String clientId, HttpClient client) {
        if (clientId == null || (clientId.equals(""))) {
            throw new IllegalArgumentException("client_id is empty");
        }
        this.clientId = clientId;
        this.client = client;
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

        return executeForJsonObjectCommon(TokenRequester.URI_YM_TOKEN, params, null, ReceiveOAuthTokenResponse.class);
    }

    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
            String redirectUri, String clientSecret) throws IOException, InsufficientScopeException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("client_secret", clientSecret));

        return executeForJsonObjectCommon(TokenRequester.URI_YM_TOKEN, params, null, ReceiveOAuthTokenResponse.class);
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
