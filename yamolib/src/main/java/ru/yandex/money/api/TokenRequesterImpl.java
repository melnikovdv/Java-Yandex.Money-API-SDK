package ru.yandex.money.api;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.rights.Permission;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
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
public class TokenRequesterImpl implements TokenRequester {

    /**
     * Кодировка для url encoding/decoding
     */
    private static final String CHARSET = "UTF-8";

    private final String clientId;

    private final YamoneyApiClient client;

    /**
     * Создает экземпляр класса.
     *
     * @param clientId идентификатор приложения в системе Яндекс.Деньги
     * @param client   настроенный HttpClient для взаимодействия с сервером Яндекс.Деньги.
     *                 Для request-payment и process-payment может понядобиться httpClient
     *                 c таймаутом до 60 секунд
     */
    public TokenRequesterImpl(final String clientId, HttpClient client) {
        if (clientId == null || (clientId.equals(""))) {
            throw new IllegalArgumentException("client_id is empty");
        }
        this.clientId = clientId;
        this.client = new YamoneyApiClient(client);
    }

    @Override
    public String authorizeUri(Collection<Permission> permissions, String redirectUri, Boolean mobileMode) {
        return authorizeUri(makeScope(permissions), redirectUri, mobileMode);
    }

    @Override
    public String authorizeUri(String scope, String redirectUri, Boolean mobileMode) {
        try {
            return (mobileMode ? URI_YM_AUTH_MOBILE : URI_YM_AUTH)
                    + "?client_id=" + clientId
                    + "&response_type=code"
                    + "&scope=" + URLEncoder.encode(scope, CHARSET)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("unsupported encoding error", e);
        }
    }

    @Override
    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
                                                       String redirectUri) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        return receiveOAuthToken(code, redirectUri, params);
    }

    @Override
    public ReceiveOAuthTokenResponse receiveOAuthToken(String code, String redirectUri,
                                                       String clientSecret) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        return receiveOAuthToken(code, redirectUri, params);
    }

    private ReceiveOAuthTokenResponse receiveOAuthToken(String code, String redirectUri,
                                                        List<NameValuePair> params) throws IOException {
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        return client.executeForJsonObjectCommon(TokenRequester.URI_YM_TOKEN, params, ReceiveOAuthTokenResponse.class);
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String makeScope(Collection<Permission> permissions) {
        if (permissions == null) {
            throw new IllegalArgumentException("permissions expected");
        }

        StringBuilder sBuilder = new StringBuilder();
        for (Permission s : permissions) {
            sBuilder = sBuilder.append(" ").append(s.value());
        }

        return sBuilder.toString().trim();
    }
}
