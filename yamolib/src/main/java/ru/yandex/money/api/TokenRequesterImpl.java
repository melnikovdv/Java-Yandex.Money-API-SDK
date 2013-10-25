package ru.yandex.money.api;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.rights.AccountInfo;
import ru.yandex.money.api.rights.OperationHistory;
import ru.yandex.money.api.rights.Permission;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

    private final String clientId;
    private final YamoneyClient client;

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
        this.client = new YamoneyClient(client);
    }

    public String authorizeUri(Collection<Permission> scope, String redirectUri, Boolean mobileMode) {
        try {
            return (mobileMode ? URI_YM_AUTH_MOBILE : URI_YM_AUTH)
                    + "?client_id=" + clientId
                    + "&response_type=code"
                    + "&scope=" + URLEncoder.encode(makeScope(scope), CHARSET)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, CHARSET);
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

        return client.executeForJsonObjectCommon(TokenRequester.URI_YM_TOKEN, params, ReceiveOAuthTokenResponse.class);
    }

    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
            String redirectUri, String clientSecret) throws IOException, InsufficientScopeException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("client_secret", clientSecret));

        return client.executeForJsonObjectCommon(TokenRequester.URI_YM_TOKEN, params, ReceiveOAuthTokenResponse.class);
    }

    public String getClientId() {
        return clientId;
    }

    private String makeScope(Collection<Permission> scope) {
        if (scope == null) {
            scope = new LinkedList<Permission>();
            scope.add(new AccountInfo());
            scope.add(new OperationHistory());
        }

        StringBuilder sBuilder = new StringBuilder();
        for (Permission s : scope) {
            sBuilder = sBuilder.append(" ").append(s.value());
        }

        return sBuilder.toString().trim();
    }
}
