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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

class YamoneyClient {

    /**
     * Кодировка для url encoding/decoding
     */
    private static final String CHARSET = "UTF-8";

    private static final Log LOGGER = LogFactory.getLog(YamoneyClient.class);

    private final HttpClient httpClient;

    public YamoneyClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    <T> T executeForJsonObjectCommon(String url, List<NameValuePair> params, Class<T> classOfT)
            throws InsufficientScopeException, IOException {

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
        if (params != null) {
            LOGGER.info("request parameters: " + params);
            httpPost.setEntity(new UrlEncodedFormEntity(params, CHARSET));
        }

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int iCode = response.getStatusLine().getStatusCode();
            if (iCode != HttpStatus.SC_OK) {
                Header wwwAuthenticate = response.getFirstHeader("WWW-Authenticate");
                LOGGER.info("http status: " + iCode + ", WWW-Authenticate: " + wwwAuthenticate);
            }
            return response;
        } catch (IOException e) {
            httpPost.abort();
            throw e;
        }
    }

    void checkCommonResponse(HttpResponse httpResp) throws
            InternalServerErrorException, InsufficientScopeException {
        int iCode = httpResp.getStatusLine().getStatusCode();

        if (iCode == HttpStatus .SC_BAD_REQUEST)
            throw new ProtocolRequestException("invalid request");
        if (iCode == HttpStatus.SC_FORBIDDEN)
            throw new InsufficientScopeException("insufficient scope");
        if (iCode == HttpStatus.SC_INTERNAL_SERVER_ERROR)
            throw new InternalServerErrorException("internal yandex.money server error");

        if (httpResp.getEntity() == null)
            throw new IllegalStateException("response http entity is empty");
    }

    <T> T parseJson(HttpEntity entity, Class<T> classOfT) throws IOException {
        InputStream is = entity.getContent();

        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(
                    FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            T result = gson.fromJson(new InputStreamReader(is, CHARSET), classOfT);
            LOGGER.info("result: " + result);
            return result;
        } catch (JsonParseException e) {
            throw new IllegalStateException("response decoding failed", e);
        }
    }


    void checkFuncResponse(HttpResponse httpResp) throws InvalidTokenException,
            InsufficientScopeException, InternalServerErrorException {

        if (httpResp.getStatusLine().getStatusCode() == 401) {
            throw new InvalidTokenException("invalid token");
        }
        checkCommonResponse(httpResp);
    }

    <T> T executeForJsonObjectFunc(String url, List<NameValuePair> params, String accessToken, Class<T> classOfT)
            throws InsufficientScopeException, IOException, InvalidTokenException {

        HttpResponse response = null;

        try {
            response = execPostRequest(new HttpPost(url), accessToken, params);
            checkFuncResponse(response);

            return parseJson(response.getEntity(), classOfT);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }
}
