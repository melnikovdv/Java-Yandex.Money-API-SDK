package ru.yandex.money.api.response.util;

/**
 * Ошибки запроса списка истории
 * <p/>
 * <p/>
 * Created: 03.12.13 22:11
 * <p/>
 *
 * @author OneHalf
 */
public enum OperationHistoryError {

    /**
     * Неверное значение параметра type.
     */
    illegal_param_type,

    /**
     * Неверное значение параметра start_record
     */
    illegal_param_start_record,

    /**
     * Неверное значение параметра records.
     */
    illegal_param_records,

    /**
     * Неверное значение параметра label
     */
    illegal_param_label,

    /**
     * Неверное значение параметра from
     */
    illegal_param_from,

    /**
     * Неверное значение параметра till.
     * Некорректный формат, или значение меньше занчения параметра from
     */
    illegal_param_till
}
