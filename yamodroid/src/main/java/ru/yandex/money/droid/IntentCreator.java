package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.rights.Permission;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * Вспомогательный класс создания Intent'ов Activity библиотеки.
 * Создан для упрощения передачи параметров.
 * @author dvmelnikov
 */

public class IntentCreator {

    /**
     * Создание Intent'а авторизации
     *
     * @param context          Activity, из которого будет стартовать Intent
     * @param clientId         идентификатор приложения в системе API Яндекс.Деньги
     * @param redirectUri      URI страницы приложения, на который OAuth-сервер
     *                         осуществляет передачу события результата авторизации. Значение этого параметра
     *                         при посимвольном сравнении должно быть идентично значению redirectUri,
     *                         указанному при регистрации приложения. При сравнении не учитываются индивидуальные
     *                         параметры приложения, которые могут быть добавлены в конец строки URI.
     * @param permissions      список запрашиваемых приложением прав
     * @param showResultDialog показывать ли в Activities библиотеки результат выполнения операций (успех/ошибка),
     *                         или приложение будет само показывать результат операции.
     * @return Intent с проставленными параметрами
     */
    public static Intent createAuth(final Activity context,
            final String clientId, final String redirectUri,
            final Collection<Permission> permissions,
            boolean showResultDialog) {
        checkMainParams(clientId, "token", context);

        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_IN_CLIENT_ID, clientId);
        intent.putExtra(AuthActivity.AUTH_IN_REDIRECT_URI, redirectUri);
        intent.putExtra(AuthActivity.AUTH_IN_SHOW_RES_DLG, showResultDialog);

        YandexMoneyImpl yandexMoney = new YandexMoneyImpl(clientId);
        String authUri =
                yandexMoney.authorizeUri(permissions, redirectUri, true);
        intent.putExtra(AuthActivity.AUTH_IN_AUTH_URI, authUri);

        return intent;
    }

    /**
     * Создание Intent'а запроса истории
     *
     * @param context     контекст приложения или родительское Activity
     * @param clientId    идентификатор приложения в системе API Яндекс.Деньги
     * @param accessToken токен авторизации пользователя
     * @return Intent с проставленными параметрами
     */
    public static Intent createHistory(final Context context,
            String clientId, String accessToken) {
        checkMainParams(clientId, accessToken, context);

        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(HistoryActivity.HISTORY_IN_CLIENT_ID, clientId);
        intent.putExtra(HistoryActivity.HISTORY_IN_ACCESS_TOKEN, accessToken);

        return intent;
    }

    /**
     * Создание Intent'а перевода на счет другого пользователя
     *
     * @param context          Activity, из которого будет стартовать Intent
     * @param clientId         идентификатор приложения в системе API Яндекс.Деньги
     * @param accessToken      токен авторизации пользователя
     * @param accountTo        номер счета получателя платежа (счет Яндекс.Денег)
     * @param amount           сумма перевода. Представляет собой число с фиксированной точкой,
     *                         два знака после точки.
     * @param comment          название платежа, отображается только в истории платежей
     *                         отправителя.
     * @param message          сообщение получателю платежа.
     * @param showResultDialog показывать ли в Activities библиотеки результат выполнения операций (успех/ошибка),
     *                         или приложение будет само показывать результат операции.
     * @return Intent с проставленными параметрами
     */
    public static Intent createPaymentP2P(final Activity context,
            String clientId, String accessToken, String accountTo,
            BigDecimal amount, String comment, String message,
            boolean showResultDialog) {
        checkMainParams(clientId, accessToken, context);

        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(PaymentActivity.PAYMENT_IN_CLIENT_ID, clientId);
        intent.putExtra(PaymentActivity.PAYMENT_IN_ACCESS_TOKEN, accessToken);
        intent.putExtra(PaymentActivity.PAYMENT_IN_P2P_FLAG, true);
        intent.putExtra(PaymentActivity.PAYMENT_IN_SHOW_RESULT_DIALOG,
                showResultDialog);

        intent.putExtra(PaymentActivity.PAYMENT_P2P_IN_ACCOUNT, accountTo);
        intent.putExtra(PaymentActivity.PAYMENT_P2P_IN_AMOUNT,
                amount.doubleValue());
        intent.putExtra(PaymentActivity.PAYMENT_P2P_IN_COMMENT, comment);
        intent.putExtra(PaymentActivity.PAYMENT_P2P_IN_MESSAGE, message);

        return intent;
    }

    /**
     * Создание Intent'а оплаты в магазин
     *
     * @param context          Activity, из которого будет стартовать Intent
     * @param clientId         идентификатор приложения в системе API Яндекс.Деньги
     * @param accessToken      токен авторизации пользователя
     * @param amount           общая сумма оплаты
     * @param patternId        идентификатор шаблона платежа
     * @param params           пользовательские параметры шаблона платежа, требуемые
     *                         магазином.
     * @param showResultDialog показывать ли в Activities библиотеки результат выполнения операций (успех/ошибка),
     *                         или приложение будет само показывать результат операции.
     * @return Intent с проставленными параметрами
     */
    public static Intent createPaymentShop(final Activity context,
            String clientId, String accessToken, BigDecimal amount,
            String patternId, Map<String, String> params,
            boolean showResultDialog) {
        checkMainParams(clientId, accessToken, context);

        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(PaymentActivity.PAYMENT_IN_CLIENT_ID, clientId);
        intent.putExtra(PaymentActivity.PAYMENT_IN_ACCESS_TOKEN, accessToken);
        intent.putExtra(PaymentActivity.PAYMENT_IN_P2P_FLAG, false);
        intent.putExtra(PaymentActivity.PAYMENT_IN_SHOW_RESULT_DIALOG,
                showResultDialog);

        PaymentShopParcelable paramsParc =
                new PaymentShopParcelable(amount, patternId, params);
        intent.putExtra(PaymentActivity.PAYMENT_SHOP_IN_PARAMS, paramsParc);

        return intent;
    }

    private static void checkMainParams(String clientId, String accessToken,
            Context context) {
        if ((clientId == null) || (clientId.equals("")))
            throw new IllegalArgumentException("client_id is empty");
        if ((accessToken == null) || (accessToken.equals("")))
            throw new IllegalArgumentException("access_token is empty");
        if (context == null)
            throw new IllegalArgumentException("context is empty");

    }
}
