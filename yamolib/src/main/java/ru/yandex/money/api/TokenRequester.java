package ru.yandex.money.api;

import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.rights.Permission;

import java.io.IOException;
import java.util.Collection;

/**
 * <p/>
 * <p/>
 * Copyright 2012 Yandex Money, All rights reserved.
 * <p/>
 * Date: 25.10.13 18:50
 *
 * @author sergeev
 */
public interface TokenRequester {
    /**
     * URI адреса для OAuth-авторизации
     */
    String URI_YM_AUTH =
            "https://sp-money.yandex.ru/oauth/authorize";
    /**
     * URI адрес для мобильной OAuth-авторизации
     */
    String URI_YM_AUTH_MOBILE =
            "https://m.sp-money.yandex.ru/oauth/authorize";
    /**
     * URI для обмена временного токена на постоянный
     */
    String URI_YM_TOKEN =
            "https://sp-money.yandex.ru/oauth/token";

    /**
     * Метод OAuth-аутентификации приложения для получения временного
     * кода (токена).
     *
     * @param scope       список запрашиваемых приложением прав.
     *                    Если параметр не задан, то будут запрашиваться следующие права:
     *                    account-info operation-history
     * @param redirectUri URI страницы приложения, на который OAuth-сервер
     *                    осуществляет передачу события результата авторизации. Значение этого параметра
     *                    при посимвольном сравнении должно быть идентично значению redirectUri,
     *                    указанному при регистрации приложения. При сравнении не учитываются индивидуальные
     *                    параметры приложения, которые могут быть добавлены в конец строки URI.
     * @param mobileMode      флаг выбора странички авторизации (мобильный сайт для true и обычный для false)
     * @return URI, по которому нужно переидти для инициации авторизации
     *
     */
    String authorizeUri(Collection<Permission> scope,
                        String redirectUri, Boolean mobileMode);

    /**
     * Метод для обмена временного кода, полученного от сервера Яндекс.Денег
     * после вызова метода authorize, на постоянный токен доступа к счету
     * пользователя.
     *
     * @param code        временный код (токен), подлежащий обмену на токен авторизации.
     *                    Присутствует в случае успешного подтверждения авторизации пользователем.
     * @param redirectUri URI, на который OAuth-сервер осуществляет передачу
     *                    события результата авторизации. Значение этого параметра при посимвольном сравнении
     *                    должно быть идентично значению redirectUri, ранее переданному в метод authorize.
     * @return экземпляр класса {@link ru.yandex.money.api.response.ReceiveOAuthTokenResponse}
     * @throws java.io.IOException          ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InsufficientScopeException   запрошена операция, на которую у
     *                                      токена нет прав.
     * @throws ru.yandex.money.api.InternalServerErrorException техническая ошибка сервера Яндекс.Денег
     */
    ReceiveOAuthTokenResponse receiveOAuthToken(String code,
                                                String redirectUri) throws IOException, InsufficientScopeException;

    ReceiveOAuthTokenResponse receiveOAuthToken(String code,
                                                String redirectUri, String clientSecret) throws IOException, InsufficientScopeException;

    /**
     * Метод возвращает идентификатор приложения в системе API Яндекс.Деньги,
     * который должен передаваться в конструкторе класса.
     *
     * @return идентификатор приложения
     */
    String getClientId();
}
