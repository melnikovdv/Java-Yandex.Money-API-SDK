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

    private String error;
    private String sender;
    private String recipient;
    private String message;
    private Boolean codepro;
    private String details;

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

    /**
     * @return возвращает номер счета отправителя перевода. Присутствует для
     * входящих переводов от других пользователей.
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return возвращает номер счета отправителя перевода. Присутствует для
     * входящих переводов от других пользователей.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @return возвращает комментарий к переводу. Присутствует для
     * переводов другим пользователям.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return возвращает признак перевод защищен кодом протекции.
     * Присутствует для переводов другим пользователям.
     */
    public Boolean getCodepro() {
        return codepro;
    }

    /**
     * @return возвращает детальное описание платежа.
     * Строка произвольного формата, может содержать любые символы и
     * переводы строк.
     */
    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "OperationDetailResponse{" +
                "error='" + error + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                ", codepro=" + codepro +
                ", details='" + details + '\'' +
                '}';
    }
}
