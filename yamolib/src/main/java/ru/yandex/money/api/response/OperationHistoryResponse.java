package ru.yandex.money.api.response;

import ru.yandex.money.api.response.util.Operation;

import java.util.List;

/**
 * <p>Класс для возврата результата метода operationHistory.
 * Содержит ошибку, если таковая была получена в результате
 * запроса, номер следующей записи для следующего запроса и
 * список операций.</p>
 * <p>При значительном количестве записей в истории список операций
 * выдается постранично. По умолчанию выдается первая страница истории.
 * Если есть хотя бы одна последующая страница, то в ответе присутствует
 * параметр nextRecord, определяющий порядковый номер ее первой записи.</p>
 * <p>Для запроса следующей страницы истории повторите запрос с теми же
 * параметрами, добавив параметр startRecord и указав в нем порядковый
 * номер первой записи следующей страницы, полученный ранее
 * из параметра nextRecord.</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */

public class OperationHistoryResponse {

    private String error;
    private Integer nextRecord;
    private List<Operation> operations;

    private OperationHistoryResponse() {
    }

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
     * <li>illegal_param_type - неверное значение параметра type метода
     * operationHistory;</li>
     * <li>illegal_param_start_record - неверное значение параметра startRecord
     * метода operationHistory;</li>
     * <li>illegal_param_records ― неверное значение параметра records;</li>
     * <li>Все прочие значения: техническая ошибка, повторите вызов операции позднее.</li>
     * </ul>
     */
    public String getError() {
        return error;
    }

    /**
     * @return
     * Порядковый номер первой записи на следующей
     * странице истории операций. Присутствует в случае наличия следующей
     * страницы истории.
     */
    public Integer getNextRecord() {
        return nextRecord;
    }

    /**
     * @return Список объектов {@link Operation}
     */
    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "OperationHistoryResponse{" +
                "error='" + error + '\'' +
                ", nextRecord='" + nextRecord + '\'' +
                ", operations=" + operations +
                '}';
    }
}
