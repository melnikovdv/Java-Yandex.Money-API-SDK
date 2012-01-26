package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Intent;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.enums.OperationHistoryType;
import ru.yandex.money.api.response.AccountInfoResponse;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.rights.Permission;
import ru.yandex.money.droid.activities.AuthActivity;
import ru.yandex.money.droid.activities.HistoryActivity;
import ru.yandex.money.droid.activities.PaymentActivity;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.preferences.Prefs;
import ru.yandex.money.droid.utils.RequestPaymentShopParcelable;
import ru.yandex.money.droid.utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author dvmelnikov
 */

public class YandexMoneyDroidImpl implements YandexMoneyDroid {

    private Activity context;
    private String clientId;
    private String redirectUri;

    private YandexMoney ym;
    private final Prefs prefs;

    /**
     * Конструктор реализации интерфейса YandexMoneyDroid
     *
     * @param parentActivity activity приложения из которого будут вызываться
     *                       функции библиотеки
     * @param clientId       идентификатор приложения в API Яндекс.Денег
     * @param redirectUri    URI, на который OAuth сервер передает результат авторизации
     */
    public YandexMoneyDroidImpl(Activity parentActivity, String clientId,
            String redirectUri) {
        this.context = parentActivity;
        this.clientId = clientId;
        this.redirectUri = redirectUri;

        prefs = new Prefs(context);
        prefs.write(LibConsts.PREF_CLIENT_ID, clientId);
        prefs.write(LibConsts.PREF_REDIRECT_URI, redirectUri);
        ym = new YandexMoneyImpl(clientId);
    }

    /**
     * Проверяет записан ли уже в системе токен для указанного при создании clientId.
     *
     * @return записан ли уже в системе токен для clientId, указанного при создании.
     *         Внимание! Токен может храниться библиотекой, но при этом быть
     *         недействительным
     */
    public boolean hasToken() {
        String token = Utils.getToken(context, clientId);
        return ((token != null) && !token.equals(""));
    }

    /**
     * <p>Инициирует получение токена доступа к API Яндекс.Денег. При успехе записывает
     * его в приложении.</p>
     * <p>За requestId в ActivityForResult отвечает константа {@link Consts#CODE_AUTH}</p>
     *
     * @param permissions список запрашиваемых приложением прав
     *                    Если параметр не задан, то будут запрашиваться следующие права:
     *                    account-info operation-history
     */
    public void authorize(Collection<Permission> permissions) {
        String sUri = ym.authorizeUri(permissions, redirectUri, true);

        Intent intent = new Intent(context, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(LibConsts.URI, sUri);
        intent.putExtra(LibConsts.PREF_CLIENT_ID, clientId);
        context.startActivityForResult(intent, Consts.CODE_AUTH);
    }

    /**
     * Удаляет из приложения токен для указанного при создании clientId
     */
    public void unauthorize() {
        Utils.writeToken(context, clientId, "");
    }

    /**
     * <p>Метод получения информации о счете.</p>
     * <p><b>Внимание!</b> Обращается к сети напрямую, поэтому следует использовать внутри
     * {@link android.os.AsyncTask} или подобного.</p>
     *
     * @return экземпляр класса {@link ru.yandex.money.api.response.AccountInfoResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    public AccountInfoResponse accountInfo() throws InsufficientScopeException,
            InvalidTokenException, IOException {
        return ym.accountInfo(Utils.getToken(context, clientId));
    }

    /**
     * Инициирует показ activity истории операциий и деталей операции
     */
    public void operationHistoryActivity() {
        if (!hasToken()) {
            Utils.showError(context, LibConsts.NOT_AUTHORIZED);
            return;
        }
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(LibConsts.PREF_CLIENT_ID, clientId);
        context.startActivity(intent);
    }

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
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     */
    public OperationHistoryResponse operationHistory()
            throws IOException, InvalidTokenException,
            InsufficientScopeException {
        return operationHistory(null);
    }

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
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     */
    public OperationHistoryResponse operationHistory(Integer startRecord)
            throws IOException, InvalidTokenException,
            InsufficientScopeException {
        return operationHistory(startRecord, null);
    }

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
     * @return возвращает экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    public OperationHistoryResponse operationHistory(Integer startRecord,
            Integer records) throws IOException,
            InvalidTokenException, InsufficientScopeException {
        return operationHistory(startRecord, records, null);
    }

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
     * @return экземпляр класса {@link OperationHistoryResponse}
     * @throws java.io.IOException        ошибка связи с сервером Яндекс.Денег
     * @throws InsufficientScopeException запрошена операция, на которую у
     *                                    токена нет прав.
     * @throws InvalidTokenException      указан несуществующий, просроченный, или отозванный токен.
     */
    public OperationHistoryResponse operationHistory(Integer startRecord,
            Integer records,
            Set<OperationHistoryType> operationsType) throws IOException,
            InvalidTokenException, InsufficientScopeException {
        if (hasToken()) {
            return ym.operationHistory(Utils.getToken(context, clientId), startRecord,
                    records, operationsType);
        }
        return null;
    }

    /**
     * Метод инициирует activity перевода средств на счета других пользователей
     * <p>За requestId в ActivityForResult отвечает константа {@link Consts#CODE_PAYMENT_P2P}</p>
     *
     * @param account номер счета получателя платежа (счет Яндекс.Денег)
     * @param amount  сумма перевода. Представляет собой число с фиксированной точкой,
     *                два знака после точки.
     * @param comment название платежа, отображается только в истории платежей
     *                отправителя.
     * @param message сообщение получателю платежа.
     */
    public void requestPaymentP2P(String account, BigDecimal amount,
            String comment,
            String message) {
        if (!hasToken()) {
            Utils.showError(context, LibConsts.NOT_AUTHORIZED);
            return;
        }
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(LibConsts.PAYMENT_P2P, true);
        intent.putExtra(LibConsts.PAYMENT_P2P_TO, account);
        intent.putExtra(LibConsts.PAYMENT_P2P_SUM, amount.doubleValue());
        intent.putExtra(LibConsts.PAYMENT_P2P_COMMENT, comment);
        intent.putExtra(LibConsts.PAYMENT_P2P_MESSAGE, message);
        intent.putExtra(LibConsts.PREF_CLIENT_ID, clientId);
        context.startActivityForResult(intent, Consts.CODE_PAYMENT_P2P);
    }

    /**
     * Метод инициирует activity перевода средств в магазин
     * <p>За requestId в ActivityForResult отвечает константа {@link Consts#CODE_PAYMENT_SHOP}</p>
     *
     * @param sum         сумма
     * @param patternId   идентификатор шаблона платежа.
     * @param params      пользовательские параметры шаблона платежа, требуемые
     * @param payWithCard способ оплаты. False для отплаты со счета и True для
     *                    оплаты с привязанной карты (требует специального права).
     */
    public void requestPaymentShop(BigDecimal sum, String patternId,
            Map<String, String> params, boolean payWithCard) {
        if (!hasToken()) {
            Utils.showError(context, LibConsts.NOT_AUTHORIZED);
            return;
        }
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(LibConsts.PAYMENT_P2P, false);
        intent.putExtra(LibConsts.PREF_CLIENT_ID, clientId);
        intent.putExtra(LibConsts.PAYMENT_SHOP_PARC,
                new RequestPaymentShopParcelable(sum, patternId,
                        params, payWithCard));
        context.startActivityForResult(intent, Consts.CODE_PAYMENT_SHOP);
    }
}
