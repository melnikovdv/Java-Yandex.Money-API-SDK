package ru.yandex.money.api.response;

import ru.yandex.money.api.enums.Status;
import ru.yandex.money.api.response.util.ProcessPaymentError;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>Класс для возврата результата метода processPayment</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */

public class ProcessPaymentResponse implements Serializable {

    private static final long serialVersionUID = -7677898505314637271L;

    private Status status;
    private String error;
    private String errorDescription;
    private String paymentId;
    private BigDecimal balance;
    private String payer;
    private String payee;
    private BigDecimal creditAmount;
    private String invoiceId;
    private String pinSerial;
    private String pinSecret;
    private Boolean testPayment;

    private ProcessPaymentResponse() {
    }

    /**
     * Метод говорящий об успехе или ошибке в проведении операции
     * @return флаг успеха проведения операции
     */
    public Boolean isSuccess() {
        return status == Status.success;
    }

    /**
     * @return возвращает код результата выполнения операции.
     * Возможные значения:
     * <ul>
     * <li>success - успешное выполнение (платеж проведен). Это конечное состояние платежа; </li>
     * <li>refused - отказ в проведении платежа, объяснение причины отказа
     * содержится в поле error. Это конечное состояние платежа; </li>
     * <li>in_progress - авторизация платежа находится в процессе выполнения.
     * Приложению следует повторить запрос с теми же параметрами спустя некоторое время;
     * все прочие значения - состояние платежа неизвестно. Приложению
     * следует повторить запрос с теми же параметрами спустя некоторое время.</li>
     * </ul>
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @return Код ошибки при проведении платежа. Присутствует только при ошибках.
     * Возможные значения:
     * <ul>
     * <li>contract_not_found - отсутствует выставленный контракт с заданным requestId;</li>
     * <li>not_enough_funds - недостаточно средств на счете плательщика;</li>
     * <li>limit_exceeded - превышен лимит на сумму операции или сумму операций за
     * период времени для выданного токена авторизации. Приложение должно
     * отобразить соответствующее диалоговое окно.</li>
     * <li>money_source_not_available - запрошенный метод платежа (money_source)
     * недоступен для данного платежа.</li>
     * <li>illegal_param_csc - отсутствует или указано недопустимое значение параметра csc;
     * payment_refused - магазин по какой-либо причине отказал в приеме платежа;</li>
     * <li>authorization_reject - в авторизации платежа отказано. Истек срок действия
     * карты, либо банк-эмитент отклонил транзакцию по карте,
     * либо превышен лимит платежной системы для данного пользователя.</li>
     * </ul>
     */
    public ProcessPaymentError getError() {
        return ProcessPaymentError.getByCode(error);
    }

    /**
     * @return возвращает идентификатор проведенного платежа.
     * Присутствует только при успешном выполнении метода.
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @return возвращает остаток на счете пользователя после
     * проведения платежа. Присутствует только при успешном выполнении метода.
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @return  возвращает номер счета плательщика. Присутствует
     * только при успешном выполнении метода.
     */
    public String getPayer() {
        return payer;
    }

    /**
     * @return возвращает номер счета получателя. Присутствует
     * только при успешном выполнении метода.
     */
    public String getPayee() {
        return payee;
    }

    /**
     * @return возвращает сумму, полученную на счет получателем.
     * Присутствует при успешном переводе средств на счет другого
     * пользователя системы.
     */
    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public Boolean isTestPayment() {
        return testPayment;
    }

    /**
     * @return Номер транзакции магазина в Яндекс.Деньгах.
     *         Присутствует при успешном выполнении платежа в магазин.
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Серийный номер (открытая часть) ПИН-кода.
     * Присутствует при успешном выполнении платежа в магазин, продающий ПИН-коды.
     *
     * Пользователь также сможет увидеть этот намер в деталях платежа на портале Яндекс.Деньги.
     *
     * @return Серийный номер ПИН-кода.
     */
    public String getPinSerial() {
        return pinSerial;
    }

    /**
     * Секрет (закрытая часть) ПИН-кода.
     * Присутствует при успешном выполнении платежа в магазин, продающий ПИН-коды
     *
     * Пользователь также сможет увидеть этот ПИН в деталях платежа на портале Яндекс.Деньги.

     * @return ПИН-код
     */
    public String getPinSecret() {
        return pinSecret;
    }

    @Override
    public String toString() {
        return "ProcessPaymentResponse{" +
                "status=" + status +
                ", error=" + error +
                ", error_description='" + errorDescription + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", balance=" + balance +
                ", payer='" + payer + '\'' +
                ", payee='" + payee + '\'' +
                ", creditAmount=" + creditAmount +
                ", invoice_id=" + invoiceId +
                ", pin_serial=" + pinSerial +
                ", test_payment=" + testPayment +
                '}';
    }
}
