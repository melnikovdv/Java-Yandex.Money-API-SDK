package ru.yandex.money.api.response.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Ошибки, возникающие на шаге подтверждения контракта
 *
 * Date: 18.11.13 20:38
 *
 * @author sergeev
 */
public enum ProcessPaymentError implements PaymentErrorCode {

    /**
     * Недостаточно средств на счете/привязанной карте.
     */
    NOT_ENOUGH_FUNDS(RequestPaymentError.NOT_ENOUGH_FUNDS.getCode()),

    /**
     * Невозможно провести платеж с указанным money_source.
     * */
    MONEY_SOURCE_NOT_AVAILABLE("money_source_not_available"),

    /**
     * В проведении платежа отказано.
     * При платеже со счета причиной может быть не принятая оферта.
     * В случае с картой возможно причина в том, что требуется 3DSecure авторизация, либо указан неправильный csc.
     */
    AUTHORIZATION_REJECT("authorization_reject"),

    /**
     * Превышен лимит платежей. Это может быть лимит токена за период,
     * общий лимит пользователя Яндекс.Денег (сумма этого лимита зависит от идентифицированности польователя)
     */
    LIMIT_EXCEEDED(RequestPaymentError.LIMIT_EXCEEDED.getCode()),

    /**
     * Истекло время действия контракта платежа (это около 15-ти минут)
     */
    CONTRACT_NOT_FOUND("contract_not_found"),

    /**
     * Аккаунт заблокирован. Нужно отправить пользователя по url из поля "account_unblock_uri"
     */
    ACCOUNT_BLOCKED(RequestPaymentError.ACCOUNT_BLOCKED.getCode()),

    /**
     * Магазин отказал в проведении платежа.
     * (Например, товара нет на складе)
     */
    PAYMENT_REFUSED(RequestPaymentError.PAYMENT_REFUSED.getCode()),

    /**
     * Некорректный формат защитного кода карты
     */
    ILLEGAL_PARAM_CSC("illegal_param_csc"),

    /**
     * Прочие ошибки
     */
    TECHNICAL_ERROR(RequestPaymentError.TECHNICAL_ERROR.getCode());

    private final String code;

    private static final Log LOG = LogFactory.getLog(ProcessPaymentError.class);

    private static Map<String,ProcessPaymentError> map;

    static {
        map = new HashMap<String, ProcessPaymentError>();
        for (ProcessPaymentError error : values()) {
            map.put(error.code, error);
        }
    }

    public static ProcessPaymentError getByCode(String code) {
        if (code == null) {
            return null;
        }

        ProcessPaymentError error = map.get(code);
        if (error != null) {
            return error;
        }

        // Если получаете эту ошибку, проверьте наличие новой версии библиотеки на github
        LOG.error("unknown error code: " + code);
        return TECHNICAL_ERROR;
    }

    ProcessPaymentError(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
