package ru.yandex.money.api.response.util;

import ru.yandex.money.api.enums.MoneyDirection;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Объект параметров операции.
 * @author dvmelnikov
 */

public class Operation {
    protected String operationId;
    protected String patternId;
    protected MoneyDirection direction;
    protected BigDecimal amount;
    protected Date datetime;
    protected String title;

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
     * @return Краткое описание операции (название
     * магазина или источник пополнения).
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operationId='" + operationId + '\'' +
                ", patternId='" + patternId + '\'' +
                ", direction='" + direction + '\'' +
                ", amount=" + amount +
                ", datetime=" + datetime +
                ", title='" + title + '\'' +
                '}';
    }
}
