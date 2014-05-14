package ru.yandex.money.api.response;

import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.enums.Status;
import ru.yandex.money.api.response.util.RequestPaymentError;
import ru.yandex.money.api.response.util.money.PaymentMethods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>Класс для возврата результата метода requestPayment</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */

public class RequestPaymentResponse implements Serializable {

    private static final long serialVersionUID = -2187147998780277482L;
    
    private Status status;
    private String error;
    private String error_description;
    private PaymentMethods moneySource;
    private String requestId;
    private String contract;
    private BigDecimal balance;
    private Boolean recipient_identified;
    private String recipient_account_type;
    private Boolean test_payment;
    private Map<String, String> contract_details;
    private String ext_action_uri;

    private RequestPaymentResponse() {
    }

    /**
     * Метод говорящий об успехе или ошибке в проведении операции
     * @return флаг успеха проведения операции
     */
    public Boolean isSuccess() {
        return status == Status.success;
    }

    /**
     * Метод проверки возможности оплаты: из кошелька или с привязанной к счету
     * банковской карты
     * @param moneySource элемент перечисления {@link MoneySource}
     * @return Возможна ли оплата способом указанным в параметрах способом
     */
    public boolean isPaymentMethodAvailable(MoneySource moneySource) {
        if (moneySource == null) {
            throw new IllegalArgumentException("Money source is empty");
        }

        if (getMoneySource() == null) {
            return false;
        }

        switch (moneySource) {
            case card:
                return getMoneySource().getCard() != null && getMoneySource().getCard().getAllowed();

            case wallet:
                return getMoneySource().getWallet() != null && getMoneySource().getWallet().getAllowed();

            default:
                return false;
        }
    }

    /**
     * Код результата выполнения операции.
     * @return перечисление {@link Status}
     */
    public Status getStatus() {
        return status;
    }

    public String getErrorDescription() {
        return error_description;
    }

    public Boolean isTestPayment() {
        return test_payment;
    }

    /**
     * @return Код ошибки при запросе платежа. Возможные значения:
     * <ul>
     * <li>illegal_params ― отсутствуют или имеют недопустимые значения
     * обязательные параметры платежа;</li>
     * <li>payment_refused ― магазин отказал в приеме платежа (например
     * пользователь попробовал заплатить за товар, которого нет в магазине).</li>
     * <li>Все прочие значения: техническая ошибка, повторите платеж
     * через несколько минут.</li>
     * </ul>
     */
    public RequestPaymentError getError() {
        return RequestPaymentError.getByCode(error);
    }

    /**
     * Доступные для приложения методы проведения платежа (wallet или card).
     * Присутствует только при успешном выполнении метода requestPayment*.
     * @return объект {@link ru.yandex.money.api.response.util.money.PaymentMethods}
     */
    public PaymentMethods getMoneySource() {
        return moneySource;
    }

    /**
     * @return идентификатор запроса платежа,
     * сгенерированный системой. Присутствует только при успешном
     * выполнении метода requestPayment*.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @return текст описания платежа (контракт).
     * Присутствует только при успешном выполнении метода requestPayment*.
     */
    public String getContract() {
        return contract;
    }

    /**
     * @return текущий остаток на счете пользователя.
     * Присутствует только при успешном выполнении метода requestPayment и при наличии у токена права account-info
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @return тип счета <u>получателя</u>. ("personal" либо "professional")
     * Присутствует только при успешном выполнении метода requestPayment при p2p переводе
     */
    public String getRecipientAccountType() {
        return recipient_account_type;
    }

    /**
     * @return Признак идентифицированности <u>получателя</u>
     * Присутствует только при успешном выполнении метода requestPayment при p2p переводе
     */
    public Boolean getRecipientIdentified() {
        return recipient_identified;
    }

    /**
     * Доп. параметры контракта.
     * Присутствуют при плаетже в магазин, если в запросе присутствовал параметр show_contract_details=true
     * @return
     */
    public Map<String, String> getContractDetails() {
        return contract_details;
    }

    /**
     * Uri, по которому нужно отправить пользователя в случае,
     * если на request-payment вернулась ошибка "ext_action_required"
     * @return
     */
    public String getExtActionUri() {
        return ext_action_uri;
    }

    @Override
    public String toString() {
        return "RequestPaymentResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                ", moneySource=" + moneySource +
                ", requestId='" + requestId + '\'' +
                ", contract='" + contract + '\'' +
                ", contract_details='" + contract_details + '\'' +
                ", balance=" + balance +
                ", test_payment=" + test_payment +
                ", recipient_identified=" + recipient_identified +
                ", recipient_account_type='" + recipient_account_type + '\'' +
                '}';
    }
}
