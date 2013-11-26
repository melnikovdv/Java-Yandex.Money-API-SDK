package ru.yandex.money.api.response.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * <p/>
 * Created: 27.11.13 0:29
 * <p/>
 *
 * @author OneHalf
 */
public enum  RequestPaymentError {

    /**
     * Недостаточно средств на счете.
     * Данная ошибка проявляется на request-payment только для p2p переводов.
     * В остальных случаях ошибка появится на process-payment
     */
    NOT_ENOUGH_FUNDS("not_enough_funds"),

    /**
     * Превышен лимит платежей. Это может быть лимит токена за период,
     * общий лимит пользователя Яндекс.Денег (сумма этого лимита зависит от идентифицированности польователя)
     * На request-payment проявляется только для p2p переводов
     */
    LIMIT_EXCEEDED("limit_exceeded"),

    /**
     * Аккаунт заблокирован. Нужно отправить пользователя по url из поля "account_unblock_uri"
     */
    ACCOUNT_BLOCKED("account_blocked"),

    /**
     * Магазин отказал в проведении платежа.
     * (Например, товара нет на складе)
     */
    PAYMENT_REFUSED("payment_refused"),

    /**
     * Прочие ошибки
     */
    TECHNICAL_ERROR("technical_error");

    private final String code;

    private static final Log LOG = LogFactory.getLog(RequestPaymentError.class);

    private static Map<String,RequestPaymentError> map;

    static {
        map = new HashMap<String, RequestPaymentError>();
        for (RequestPaymentError error : values()) {
            map.put(error.code, error);
        }
    }

    public static RequestPaymentError getByCode(String code) {
        if (code == null) {
            return null;
        }

        RequestPaymentError error = map.get(code);
        if (error != null) {
            return error;
        }

        // Если получаете эту ошибку, проверьте наличие новой версию библиотеки на github
        LOG.error("unknown error code: " + code);
        return TECHNICAL_ERROR;
    }

    RequestPaymentError(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }
}
