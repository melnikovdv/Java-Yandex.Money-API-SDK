package ru.yandex.money.api.response.util;

import ru.yandex.money.api.enums.MoneyDirection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Объект параметров операции.
 * @author dvmelnikov
 */

public class Operation implements Serializable {

    private static final long serialVersionUID = -8165150792250463801L;
    
    protected String operationId;
    protected String patternId;
    protected MoneyDirection direction;
    protected BigDecimal amount;
    protected Date datetime;
    protected String title;
    protected String sender;
    protected String recipient;
    protected String message;
    protected Boolean codepro;
    protected String details;
    protected String label;

    protected Operation() {
    }

    /**
     * @return идентификатор операции
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * @return
     * <p>Идентификатор шаблона платежа,
     * по которому совершен платеж. Присутствует только для платежей.
     * </p>
     * <p>Для перевода между счетами пользователей значение: p2p.
     * В остальных случая это операции с магазинами.</p>
     */
    public String getPatternId() {
        return patternId;
    }

    /**
     * Направление движения средств
     * @return объект {@link ru.yandex.money.api.enums.MoneyDirection}
     */
    public MoneyDirection getDirection() {
        return direction;
    }

    /**
     * @return Сумма операции
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return Дата и время совершения операции.
     */
    public Date getDatetime() {
        return datetime;
    }

    /**
     * @return Краткое описание операции (название магазина или источник пополнения).
     */
    public String getTitle() {
        return title;
    }

    /**
     * Поле присутствует только при запросе operation-history с параметром details=true,
     * или при запросе operation-detail
     *
     * @return возвращает номер счета отправителя перевода. Присутствует для
     * входящих переводов от других пользователей.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Поле присутствует только при запросе operation-history с параметром details=true,
     * или при запросе operation-detail
     *
     * @return возвращает номер счета отправителя перевода. Присутствует для
     * входящих переводов от других пользователей.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Поле присутствует только при запросе operation-history с параметром details=true,
     * или при запросе operation-detail
     *
     * @return возвращает комментарий к переводу. Присутствует для
     * переводов другим пользователям.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Поле присутствует только при запросе operation-history с параметром details=true,
     * или при запросе operation-detail
     *
     * @return возвращает признак перевод защищен кодом протекции.
     * Присутствует для переводов другим пользователям.
     */
    public Boolean getCodepro() {
        return codepro;
    }

    /**
     * Поле присутствует только при запросе operation-history с параметром details=true,
     * или при запросе operation-detail
     *
     * @return возвращает детальное описание платежа.
     * Строка произвольного формата, может содержать любые символы и
     * переводы строк.
     */
    public String getDetails() {
        return details;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operationId='" + operationId + '\'' +
                ", patternId='" + patternId + '\'' +
                ", title='" + title + '\'' +
                ", direction=" + direction +
                ", amount=" + amount +
                ", datetime=" + datetime +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                ", codepro=" + codepro +
                ", details='" + details + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
