package ru.yandex.money.api.response;

/**
 * <p>Класс для возврата результата метода receiveOAuthToken</p>
 * <b>Внимание</b>: при неуспешном результате операции все поля, кроме error и
 * status (если таковые присутствуют), равны null
 * @author dvmelnikov
 */

public class ReceiveOAuthTokenResponse {

    private String error;
    private String accessToken;

    private ReceiveOAuthTokenResponse() {
    }

    /**
     * Метод говорящий об успехе или ошибке в проведении операции
     * @return флаг успеха проведения операции
     */
    public Boolean isSuccess() {
        return error == null;
    }

    /**
     * @return Код ошибки. Присутствует в случае ошибки.
     * Возможные значения:
     * <ul>
     *  <li>invalid_request Обязательные параметры запроса отсутствуют
     *  или имеют некорректные или недопустимые значения</li>
     *  <li>unauthorized_client Неверное значение параметра client_id, либо
     *  приложение не имеет права запрашивать авторизацию</li>
     *  <li>(например, его client_id заблокирован системой
     * "Яндекс.Деньги")</li>
     *  <li>access_denied В выдаче access_token отказано. Временный токен
     *  не выдавался системой, либо просрочен, либо по этому
     *  временному токену уже выдан access_token (повторный
     *  запрос токена авторизации с тем же временным токеном)</li>
     * </ul>
     */
    public String getError() {
        return error;
    }

    /**
     * @return токен пользователя
     */
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "ReceiveOAuthTokenResponse{" +
                "error='" + error + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
