package ru.yandex.money.api.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Данные по статистике входящих платежей по указанной метке.
 * <p/>
 * <p/>
 * Created: 26.10.13 22:30
 * <p/>
 *
 * @author OneHalf
 */
public class FundraisingStatsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String error;
    private BigDecimal sum;
    private Long count;
    private Date first_ts;
    private Date last_ts;

    private FundraisingStatsResponse() {}

    /**
     * Метод говорящий об успехе или ошибке в проведении операции
     * @return флаг успеха проведения операции
     */
    public Boolean isSuccess() {
        return error == null;
    }

    /**
     * @return Код ошибки. Присутствует при ошибке выполнения запроса.
     * Возможные значения:
     * <ul>
     * <li>illegal_param_label - неверное значение параметра label</li>
     * <li>Все прочие значения: техническая ошибка, повторите вызов операции позднее.</li>
     * </ul>
     */
    public String getError() {
        return error;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public Long getCount() {
        return count;
    }

    /**
     * @return Дата первого платежа по запрошенной метке.
     * Поле отсутствует, если таких платежей еще не было
     */
    public Date getFirstTs() {
        return first_ts;
    }

    /**
     * @return Дата последнего платежа по запрошенной метке.
     * Поле отсутствует, если таких платежей еще не было
     */
    public Date getLastTs() {
        return last_ts;
    }

    @Override
    public String toString() {
        return "FundraisingStatsResponse{" +
                "error='" + error + '\'' +
                ", sum=" + sum +
                ", count=" + count +
                ", first_ts=" + first_ts +
                ", last_ts=" + last_ts +
                '}';
    }
}
