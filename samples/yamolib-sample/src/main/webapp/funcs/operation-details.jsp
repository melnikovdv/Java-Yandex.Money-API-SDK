<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.YandexMoney" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.response.OperationDetailResponse" %>
<%@ page import="ru.yandex.money.api.response.OperationHistoryResponse" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../styles.css">
    <title>Пример вызова API-функции account-info</title>
</head>
<body>
<div id="main">
    <h3 id="header">Пример вызова API-функции operation-details</h3>

    <%
        YandexMoney ym = new YandexMoneyImpl(Settings.CLIENT_ID);
        String token = (String) session.getAttribute("token");
        try {
            OperationHistoryResponse hist = ym.operationHistory(token, 1, 1);

            if (hist.getOperations().size() > 0) {
                OperationDetailResponse resp = ym.operationDetail(token,
                        hist.getOperations().get(0).getOperationId());

                if (resp.isSuccess()) {
    %>

    <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/funcs/operation-details.jsp">тут</a>.
        Результат выполнения:
    </p>
    <p class="code">
        Идентификатор операции: <%= resp.getOperationId() %>; <br/>
        Идентификатор шаблона платежа: <%= resp.getPatternId() %>; <br/>
        Направление движения средств: <%= resp.getDirection() %>; <br/>
        Сумма операции : <%= resp.getAmount() %> <br/>
        Дата: <%= resp.getDatetime() %>; <br/>
        Краткое описание операции: <%= resp.getTitle() %>; <br/>
        Отправитель: <%= resp.getSender() %>; <br/>
        Получатель: <%= resp.getRecipient() %>; <br/>
        Комментарий: <%= resp.getMessage() %>; <br/>
        Перевод защищен кодом протекции: <%= resp.getCodepro() %>; <br/>
        Детальная информация: <%= resp.getDetails() %>;
    </p>
            <% } else {
                out.println("В ответе получена ошибка: " + resp.getError());
            }
        } else
            out.println(
                    "Ни одной операции со счетом текущего пользователя не найдено");
        } catch (Exception e) {
            out.println("При выполнении возникла ошибка: " + e.getMessage());
        }
    %>
    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
</body>

</html>