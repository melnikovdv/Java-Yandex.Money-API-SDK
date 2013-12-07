package ru.yandex.money.api.notifications;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import ru.yandex.money.api.YamoneyAccount;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Уведомление о входящем переводе
 */
public final class IncomingTransfer {

    private final String operationId;

    private final BigDecimal amount;

    private final int currency;

    private final Date datetime;

    private final YamoneyAccount sender;

    private final boolean codepro;

    private final String label;

    IncomingTransfer(String operationId, BigDecimal amount, int currency,
                     Date datetime, YamoneyAccount sender, boolean codepro, String label) {
        this.operationId = operationId;
        this.amount = amount;
        this.currency = currency;
        this.datetime = datetime;
        this.sender = sender;
        this.codepro = codepro;
        this.label = label;
    }

    public static IncomingTransfer createByParameters(Map<String, String> parametersMap) {
        // получить значения параметров уведомления
        return new IncomingTransfer(
                parametersMap.get("operation_id"),
                new BigDecimal(parametersMap.get("amount")),
                Integer.parseInt(parametersMap.get("currency")),
                XMLGregorianCalendarImpl.parse(parametersMap.get("datetime")).toGregorianCalendar().getTime(),
                new YamoneyAccount(parametersMap.get("sender")),
                Boolean.parseBoolean(parametersMap.get("codepro")),
                parametersMap.get("label"));
    }

    /**
     * Идентификатор операции в истории счета получателя
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * Сумма операции.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Код валюты счета пользователя. Всегда 643 (рубль РФ согласно ISO 4217).
     */
    public int getCurrency() {
        return currency;
    }

    /**
     *  Дата и время совершения перевода.
     */
    public Date getDatetime() {
        return datetime;
    }

    /**
     *  Номер счета отправителя перевода.
     */
    public YamoneyAccount getSender() {
        return sender;
    }

    /**
     * Перевод защищен кодом протекции.
     */
    public boolean isCodepro() {
        return codepro;
    }

    /**
     *  Метка платежа. Если метки у платежа нет, параметр содержит пустую строку.
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "IncomingTransfer{" +
                "operationId='" + operationId + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", datetime=" + datetime +
                ", sender='" + sender + '\'' +
                ", codepro=" + codepro +
                ", label='" + label + '\'' +
                '}';
    }
}
