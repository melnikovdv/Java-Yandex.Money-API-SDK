package ru.yandex.money.droid;

/**
 * Класс, который содержит имена параметров, которые можно получить в результате
 * вызовов Activity библиотеки.
 * @author dvmelnikov
 */

public class ActivityParams {
    public static final String AUTH_OUT_IS_SUCCESS = "ru.yandex.money.droid.is_sucess";
    public static final String AUTH_OUT_ACCESS_TOKEN = "ru.yandex.money.droid.access_token";
    public static final String AUTH_OUT_ERROR = "ru.yandex.money.droid.error";
    public static final String AUTH_OUT_EXCEPTION = "ru.yandex.money.droid.exception";

    public static final String HISTORY_OUT_IS_SUCCESS = "ru.yandex.money.droid.is_success";
    public static final String HISTORY_OUT_ERROR = "ru.yandex.money.droid.error";
    public static final String HISTORY_OUT_EXCEPTION = "ru.yandex.money.droid.exception";

    public static final String PAYMENT_OUT_IS_SUCCESS = "ru.yandex.money.droid.is_success";
    public static final String PAYMENT_OUT_ERROR = "ru.yandex.money.droid.error";
    public static final String PAYMENT_OUT_OPERATION_ID = "ru.yandex.money.droid.operation_id";
}
