package ru.yandex.money.api.response;

import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.enums.Status;
import ru.yandex.money.api.response.util.money.PaymentMethods;

import java.math.BigDecimal;

/**
 * <p>Класс для возврата результата метода requestPayment</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */

public class RequestPaymentResponse {

    private Status status;
    private String error;
    private PaymentMethods moneySource;
    private String requestId;
    private String contract;
    private BigDecimal balance;

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
    public boolean isPaymentMethodAvailalable(MoneySource moneySource) {
        if (moneySource == null)
             throw new IllegalArgumentException("Money source is empty");

        if (getMoneySource() == null)
            return false;
        else {
            if (moneySource == MoneySource.card) {
                if (getMoneySource().getCard() == null)
                    return false;
                else
                    return getMoneySource().getCard().getAllowed();
            }

            if (moneySource == MoneySource.wallet) {
                if (getMoneySource().getWallet() == null)
                    return false;
                else
                    return getMoneySource().getWallet().getAllowed();
            }

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
    public String getError() {
        return error;
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
     * Присутствует только при успешном выполнении метода requestPayment*.
     */
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "RequestPaymentResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", moneySource=" + moneySource +
                ", requestId='" + requestId + '\'' +
                ", contract='" + contract + '\'' +
                ", balance=" + balance +
                '}';
    }
}
