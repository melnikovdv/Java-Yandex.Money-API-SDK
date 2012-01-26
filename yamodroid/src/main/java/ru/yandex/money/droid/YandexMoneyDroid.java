package ru.yandex.money.droid;

import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.AccountInfoResponse;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.rights.Permission;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author dvmelnikov
 */

public interface YandexMoneyDroid {
    /**
     * Проверяет записан ли уже в системе токен для указанного при создании clientId.
     *
     * @return записан ли уже в системе токен для clientId, указанного при создании.
     *         Внимание! Токен может храниться библиотекой, но при этом быть
     *         недействительным
     */
    boolean hasToken();

    /**
     * <p>Инициирует получение токена доступа к API Яндекс.Денег. При успехе записывает
     * его в приложении.</p>
     * <p>За requestId в ActivityForResult отвечает константа {@link ru.yandex.money.droid.Consts#CODE_AUTH}</p>
     *
     * @param permissions список запрашиваемых приложением прав
     *                    Если параметр не задан, то будут запрашиваться следующие права:
     *                    account-info operation-history
     */
    void authorize(Collection<Permission> permissions);

    /**
     * Удаляет из приложения токен для указанного при создании clientId
     */
    void unauthorize();

    /**
     * <p>Метод получения информации о счете.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @return экземпляр класса {@link ru.yandex.money.api.response.AccountInfoResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws ru.yandex.money.api.InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    AccountInfoResponse accountInfo() throws InsufficientScopeException,
            InvalidTokenException, IOException;

    /**
     * Инициирует показ activity истории операциий и деталей операции
     */
    void operationHistoryActivity();

    /**
     * <p>Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @return экземпляр класса {@link ru.yandex.money.api.response.OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws ru.yandex.money.api.InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     */
    OperationHistoryResponse operationHistory()
            throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * <p>Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @param startRecord запись с которой начинать вывод
     * @return экземпляр класса {@link ru.yandex.money.api.response.OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws ru.yandex.money.api.InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     */
    OperationHistoryResponse operationHistory(Integer startRecord)
            throws IOException, InvalidTokenException,
            InsufficientScopeException;

    /**
     * <p>Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @param startRecord запись с которой начинать вывод
     * @param records     количество записей
     * @return возвращает экземпляр класса {@link ru.yandex.money.api.response.OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws ru.yandex.money.api.InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    OperationHistoryResponse operationHistory(Integer startRecord,
            Integer records) throws IOException,
            InvalidTokenException, InsufficientScopeException;

    /**
     * <p>Метод позволяет просматривать историю операций (полностью или частично)
     * в постраничном режиме. Записи истории выдаются в обратном хронологическом
     * порядке. Операции выдаются для постраничного отображения (ограниченное количество).
     * Требуемые права токена: operation-history.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @param startRecord    integer порядковый номер первой записи в выдаче. По умолчанию
     *                       выдается с первой записи
     * @param records        количество запрашиваемых записей истории операций.
     *                       Допустимые значения: от 1 до 100, по умолчанию 30.
     * @param operationsType перечень типов операций, которые требуется отобразить.
     *                       Типы операций перечисляются через пробел. В случае, если параметр
     *                       отсутствует, выводятся все операции. Возможные значения: payment deposition.
     *                       В качестве разделителя элементов списка используется пробел, элементы списка
     *                       чувствительны к регистру.
     * @return экземпляр класса {@link ru.yandex.money.api.response.OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws ru.yandex.money.api.InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws ru.yandex.money.api.InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    OperationHistoryResponse operationHistory(Integer startRecord,
            Integer records,
            Set<OperationHistoryType> operationsType) throws IOException,
            InvalidTokenException, InsufficientScopeException;

    /**
     * Метод инициирует activity перевода средств на счета других пользователей
     * <p>За requestId в ActivityForResult отвечает константа {@link ru.yandex.money.droid.Consts#CODE_PAYMENT_P2P}</p>
     *
     * @param account номер счета получателя платежа (счет Яндекс.Денег)
     * @param amount  сумма перевода. Представляет собой число с фиксированной точкой,
     *                два знака после точки.
     * @param comment название платежа, отображается только в истории платежей
     *                отправителя.
     * @param message сообщение получателю платежа.
     */
    void requestPaymentP2P(String account, BigDecimal amount,
            String comment,
            String message);

    /**
     * Метод инициирует activity перевода средств в магазин
     * <p>За requestId в ActivityForResult отвечает константа {@link ru.yandex.money.droid.Consts#CODE_PAYMENT_SHOP}</p>
     *
     * @param sum         сумма
     * @param patternId   идентификатор шаблона платежа.
     * @param params      пользовательские параметры шаблона платежа, требуемые
     * @param payWithCard способ оплаты. False для отплаты со счета и True для
     *                    оплаты с привязанной карты (требует специального права).
     */
    void requestPaymentShop(BigDecimal sum, String patternId,
            Map<String, String> params, boolean payWithCard);
}
