package ru.yandex.money.api;

import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.*;
import ru.yandex.money.api.rights.Permission;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс общения приложения с API Яндекс.Деньги.
 *
 * @author dvmelnikov
 */

public interface YandexMoney {

    /**
     * URI API сервера Яндекс.Денег
     */
    public static String URI_YM_API = "https://money.yandex.ru/api";

    /**
     * URI адреса для OAuth-авторизации
     */
    public static String URI_YM_AUTH =
            "https://sp-money.yandex.ru/oauth/authorize";

    /**
     * URI для обмена временного токена на постоянный
     */
    public static String URI_YM_TOKEN =
            "https://sp-money.yandex.ru/oauth/token";

    /**
     * Метод OAuth-аутентификации приложения для получения временного
     * кода (токена).
     *
     * @param scope       список запрашиваемых приложением прав. В качестве разделителя
     *                    элементов списка используется пробел, элементы списка чувствительны к регистру.
     *                    Примеры прав можно посмотреть в package {@link ru.yandex.money.api.rights}.
     *                    Если параметр не задан, то будут запрашиваться следующие права:
     *                    account-info operation-history
     * @param redirectUri URI страницы приложения, на который OAuth-сервер
     *                    осуществляет передачу события результата авторизации. Значение этого параметра
     *                    при посимвольном сравнении должно быть идентично значению redirectUri,
     *                    указанному при регистрации приложения. При сравнении не учитываются индивидуальные
     *                    параметры приложения, которые могут быть добавлены в конец строки URI.
     * @return возвращает URI, по которому нужно переидти для
     *         инициации аутентификации
     *         ошибка кодирования параметров uri
     */
    public String authorizeUri(Collection<Permission> scope,
            String redirectUri);

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
     * @return возвращает экземпляр класса {@link ReceiveOAuthTokenResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public ReceiveOAuthTokenResponse receiveOAuthToken(String code,
            String redirectUri) throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * Метод получения информации о текущем состоянии счета пользователя.
     * Требуемые права токена: account-info
     *
     * @param accessToken string токен авторизации пользователя
     * @return возвращает экземпляр класса {@link AccountInfoResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public AccountInfoResponse accountInfo(String accessToken)
            throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.
     *
     * @param accessToken    токен авторизации пользователя
     * @param startRecord    integer порядковый номер первой записи в выдаче. По умолчанию
     *                       выдается с первой записи
     * @param records        количество запрашиваемых записей истории операций.
     *                       Допустимые значения: от 1 до 100, по умолчанию 30.
     * @param operationsType перечень типов операций, которые требуется отобразить.
     *                       Типы операций перечисляются через пробел. В случае, если параметр
     *                       отсутствует, выводятся все операции. Возможные значения: payment deposition.
     *                       В качестве разделителя элементов списка используется пробел, элементы списка
     *                       чувствительны к регистру.
     * @return возвращает экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public OperationHistoryResponse operationHistory(String accessToken,
            Integer startRecord, Integer records,
            Set<OperationHistoryType> operationsType) throws IOException,
            InvalidTokenException, InsufficientScopeException;

    /**
     * Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.
     *
     * @param accessToken токен авторизации пользователя
     * @return возвращает экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public OperationHistoryResponse operationHistory(String accessToken)
            throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.
     *
     * @param accessToken токен авторизации пользователя
     * @param startRecord запись с которой начинать вывод
     * @param records     количество записей
     * @return возвращает экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public OperationHistoryResponse operationHistory(String accessToken,
            Integer startRecord, Integer records)
            throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * Метод получения детальной информации по операции из истории.
     *
     * @param accessToken токен авторизации пользователя
     * @param operationId идентификатор операции. Значение параметра соответствует
     *                    либо значению поля operationId ответа метода operationHistory, либо, в
     *                    случае если запрашивается история счета плательщика, значению поля
     *                    paymentId ответа метода processPayment.
     * @return возвращает экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public OperationDetailResponse operationDetail(String accessToken,
            String operationId) throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * <p>Запрос p2p перевода другому пользователю.</p>
     * <b>Внимание</b>: перевод на счет пользователя, чей токен указывается в параметрах,
     * невозможен. Т.е. самому себе делать переводы нельзя.
     *
     * @param accessToken токен авторизации пользователя
     * @param to          номер счета получателя платежа (счет Яндекс.Денег)
     * @param amount      сумма перевода. Представляет собой число с фиксированной точкой,
     *                    два знака после точки.
     * @param comment     название платежа, отображается только в истории платежей
     *                    отправителя.
     * @param message     сообщение получателю платежа.
     * @return возвращает экземпляр класса {@link RequestPaymentResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public RequestPaymentResponse requestPaymentP2P(String accessToken,
            String to, BigDecimal amount, String comment,
            String message) throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * Запрос оплаты в магазин.
     *
     * @param accessToken токен авторизации пользователя
     * @param patternId   идентификатор шаблона платежа.
     * @param params      пользовательские параметры шаблона платежа, требуемые
     *                    магазином.
     * @return возвращает экземпляр класса {@link RequestPaymentResponse}
     * @throws IOException                ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public RequestPaymentResponse requestPaymentShop(String accessToken,
            String patternId, Map<String, String> params) throws IOException,
            InvalidTokenException, InsufficientScopeException;

    /**
     * Метод подтверждения платежа с оплатой с привязанной карты.
     *
     * @param accessToken токен авторизации пользователя
     * @param requestId   идентификатор запроса (requestId), полученный с
     *                    помощью методов requestPayment*.
     * @param csc         Card Security Code, CVV2/CVC2-код привязанной
     *                    банковской карты пользователя.
     * @return возвращает экземпляр класса {@link ProcessPaymentResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public ProcessPaymentResponse processPaymentByCard(String accessToken,
            String requestId, String csc) throws IOException,
            InsufficientScopeException, InvalidTokenException;

    /**
     * Метод подтверждения платежа c оплатой с кошелька пользователя.
     *
     * @param accessToken токен авторизации пользователя
     * @param requestId   идентификатор запроса (requestId), полученный с
     *                    помощью методов requestPayment*.
     * @return возвращает экземпляр класса {@link ProcessPaymentResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InternalServerErrorException    техническая ошибка сервера Яндекс.Денег
     */
    public ProcessPaymentResponse processPaymentByWallet(String accessToken,
            String requestId) throws IOException, InsufficientScopeException,
            InvalidTokenException;

    /**
     * Метод возвращает идентификатор приложения в системе Яндекс.Деньги,
     * который должен передаваться в конструкторе класса.
     *
     * @return возвращает идентификатор приложения
     */
    public String getClientId();
}
