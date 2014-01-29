package ru.yandex.money.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class YamoneyApiClient {

    /**
     * Кодировка для url encoding/decoding
     */
    private static final String CHARSET = "UTF-8";

    private static final Log LOGGER = LogFactory.getLog(YamoneyApiClient.class);
    private static final String USER_AGENT = "yamolib";

    private final HttpClient httpClient;

    public YamoneyApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    static HttpClient createHttpClient(int socketTimeout) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 4000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), socketTimeout);
        return httpClient;
    }

    <T> T executeForJsonObjectCommon(String url, List<NameValuePair> params, Class<T> classOfT) throws IOException {

        HttpResponse response = null;
        try {
            response = execPostRequest(new HttpPost(url), params);
            checkCommonResponse(response);

            return parseJson(response.getEntity(), classOfT);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    HttpResponse execPostRequest(HttpPost httpPost, String accessToken, List<NameValuePair> params) throws IOException {
        httpPost.addHeader("Authorization", "Bearer " + accessToken);
        return execPostRequest(httpPost, params);
    }

    HttpResponse execPostRequest(HttpPost httpPost, List<NameValuePair> params) throws IOException {
        logParameters(httpPost.getURI(), params);
        httpPost.setEntity(new UrlEncodedFormEntity(params, CHARSET));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int iCode = response.getStatusLine().getStatusCode();
            if (iCode != HttpStatus.SC_OK) {
                Header wwwAuthenticate = response.getFirstHeader("WWW-Authenticate");
                LOGGER.info("http status: " + iCode + (wwwAuthenticate == null ? "" : ", " + wwwAuthenticate));
            }
            return response;
        } catch (IOException e) {
            httpPost.abort();
            throw e;
        }
    }

    private void logParameters(URI uri, List<NameValuePair> params) {
        if (!LOGGER.isInfoEnabled()) {
            return;
        }
        // Пишем в логи все параметры, кроме кода карточки
        Map<String, String> paramsForLog = new HashMap<String, String>();
        for (NameValuePair param : params) {
            if (param.getName().equalsIgnoreCase("csc")) {
                paramsForLog.put(param.getName(), "***");
            }
            else {
                paramsForLog.put(param.getName(), param.getValue());
            }
        }
        LOGGER.info("request url '" + uri +"' with parameters: " + paramsForLog);
    }

    void checkCommonResponse(HttpResponse httpResp) throws InternalServerErrorException {

        switch (httpResp.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_BAD_REQUEST:
                throw new ProtocolRequestException("invalid request");
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                throw new InternalServerErrorException("internal yandex.money server error");
        }

        if (httpResp.getEntity() == null) {
            throw new IllegalStateException("response http entity is empty");
        }
    }

    <T> T parseJson(HttpEntity entity, Class<T> classOfT) throws IOException {
        InputStream is = entity.getContent();

        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(
                    FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            T result = gson.fromJson(new InputStreamReader(is, CHARSET), classOfT);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("result: " + result);
            }
            return result;
        } catch (JsonParseException e) {
            throw new IllegalStateException("response decoding failed", e);
        }
    }


    void checkApiCommandResponse(HttpResponse httpResp) throws InvalidTokenException,
            InsufficientScopeException, InternalServerErrorException {

        if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new InvalidTokenException("invalid token");
        }
        if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            throw new InsufficientScopeException("insufficient scope");
        }
        checkCommonResponse(httpResp);
    }

    <T> T executeForJsonObjectFunc(CommandUrlHolder urlHolder, String commandName, List<NameValuePair> params,
                                   String accessToken, Class<T> classOfT)
            throws InsufficientScopeException, IOException, InvalidTokenException {

        HttpResponse response = null;

        try {

            response = execPostRequest(new HttpPost(urlHolder.getUrlForCommand(commandName)),
                    accessToken, params(params, urlHolder));

            checkApiCommandResponse(response);

            return parseJson(response.getEntity(), classOfT);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private List<NameValuePair> params(List<NameValuePair> params, CommandUrlHolder urlHolder) {
        List<NameValuePair> result = new ArrayList<NameValuePair>(params);
        result.addAll(urlHolder.getAdditionalParams());
        return result;
    }
}
