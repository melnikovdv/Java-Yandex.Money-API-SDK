package ru.yandex.money.api.response;

import ru.yandex.money.api.response.util.Operation;

/**
 * <p>Класс для возврата результата метода operationDetail. Содержит
 * подробную информацию о конкретной операции из списка.</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */
public class OperationDetailResponse extends Operation {

    private static final long serialVersionUID = -8469292472444798257L;

    private String error;

    private OperationDetailResponse() {
        super();
    }

    /**
     * Метод говорящий об успехе или ошибке в проведении операции
     * @return флаг успеха проведения операции
     */
    public Boolean isSuccess() {
        return error == null;
    }

    /**
     * @return код ошибки, присутствует при ошибке выполнения запроса.
     * Возможные значения:
     * <ul>
     * <li>illegal_param_operation_id  неверное значение
     * параметра operation_id запроса.</li>
     * </ul>
     */
    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return super.toString();
        }
        return "OperationDetailResponse{" +
                "error='" + error + "\'}";
    }
}
