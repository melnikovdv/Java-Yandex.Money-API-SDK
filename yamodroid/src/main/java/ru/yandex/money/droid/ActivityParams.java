package ru.yandex.money.droid;

/**
 * Класс, который содержит имена параметров, которые можно получить в результате
 * вызовов Activity библиотеки.
 * @author dvmelnikov
 */

public class ActivityParams {
    private static String COMMON_OUT_IS_SUCCESS = "ru.yandex.money.droid.is_sucess";
    private static String COMMON_OUT_ERROR = "ru.yandex.money.droid.error";
    private static String COMMON_OUT_EXCEPTION = "ru.yandex.money.droid.exception";

    public static final String AUTH_OUT_IS_SUCCESS = COMMON_OUT_IS_SUCCESS;
    public static final String AUTH_OUT_ACCESS_TOKEN = "ru.yandex.money.droid.access_token";
    public static final String AUTH_OUT_ERROR = COMMON_OUT_ERROR;
    public static final String AUTH_OUT_EXCEPTION = COMMON_OUT_EXCEPTION;

    public static final String HISTORY_OUT_IS_SUCCESS = COMMON_OUT_IS_SUCCESS;
    public static final String HISTORY_OUT_ERROR = COMMON_OUT_ERROR;
    public static final String HISTORY_OUT_EXCEPTION = COMMON_OUT_EXCEPTION;

    public static final String HISTORY_DETAIL_OUT_IS_SUCCESS = COMMON_OUT_IS_SUCCESS;
    public static final String HISTORY_DETAIL_OUT_ERROR = COMMON_OUT_ERROR;
    public static final String HISTORY_DETAIL_OUT_EXCEPTION = COMMON_OUT_EXCEPTION;

    public static final String PAYMENT_OUT_IS_SUCCESS = COMMON_OUT_IS_SUCCESS;
    public static final String PAYMENT_OUT_ERROR = COMMON_OUT_ERROR;
    public static final String PAYMENT_OUT_OPERATION_ID = "ru.yandex.money.droid.operation_id";
    public static final String PAYMENT_OUT_EXCEPTION = COMMON_OUT_EXCEPTION;
}
