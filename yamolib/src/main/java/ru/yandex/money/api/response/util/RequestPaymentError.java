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
public enum  RequestPaymentError implements PaymentErrorCode {

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
     * В авторизации платежа отказано. Возможные причины:
     * <ul>
     *     <li>транзакция с текущими параметрами запрещена для данного пользователя;</li>
     *     <li>пользователь не принял Соглашение об использовании сервиса «Яндекс.Деньги».</li>
     * </ul>
     */
    AUTHORIZATION_REJECT("authorization_reject"),

    /**
     * Магазин отказал в проведении платежа.
     * (Например, товара нет на складе)
     */
    PAYMENT_REFUSED("payment_refused"),

    /**
     * Обязательные параметры платежа отсутствуют или имеют недопустимые значения.
     */
    ILLEGAL_PARAMS("illegal_params"),

    /**
     * Недопустимое значение параметра label.
     * (если параметры присутствует, то должен быть не пустым и не содержать больше 64-х символов)
     */
    ILLEGAL_PARAM_LABEL("illegal_param_label"),

    /**
     * При p2p переводе по привязанному номеру телефона указан номер телефона не связанный
     * со счетом пользователя или получателя платежа.
     */
    PHONE_UNKNOWN("phone_unknown"),

    /**
     * Требуется отправить пользователя на uri, указанный в поле ext_action_uri.
     * Только после этого пользователь сможет выполнить запрошенный платеж.
     *
     * На текущий момент ошибка проявляется только при p2p-переводе или при платеже в магазин-нерезидент РФ,
     * в случае, если плательщик не заполнял свои персональные данные на сайте Яндекс.Денег
     */
    EXT_ACTION_REQUIRED("ext_action_required"),

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

    @Override
    public String getCode() {
        return code;
    }
}
