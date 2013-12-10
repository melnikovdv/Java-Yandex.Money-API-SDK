<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.response.OperationHistoryResponse" %>
<%@ page import="ru.yandex.money.api.response.util.Operation" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../styles.css">
    <title>Пример вызова API-функции operation-history</title>
</head>
<body>
<div id="main">
    <h3 id="header">Пример вызова API-функции operation-history</h3>

    <%
        ru.yandex.money.api.YandexMoney ym =
                new YandexMoneyImpl(Settings.CLIENT_ID);
        String token = (String) session.getAttribute("token");
        try {
            OperationHistoryResponse resp = ym.operationHistory(token, 1, 5);

            if (!resp.isSuccess())
                out.println("В ответе получена ошибка: " + resp.getError());
            else {
    %>
    <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/funcs/operation-history.jsp">тут</a>.
        Результат выполнения:
    </p>
    <p class="code">
        Следующая запись: <%= resp.getNextRecord() %> <br/>
        Операции:
    </p>
        <ul class="code">
            <% for (Operation op : resp.getOperations()) { %>
            <li>
                Идентификатор операции: <%= op.getOperationId() %>;
                Дата: <%= op.getDatetime() %>;
                Краткое описание операции: <%= op.getTitle() %>;
                Идентификатор шаблона платежа: <%= op.getPatternId() %>;
                Направление движения средств: <%= op.getDirection() %>;
                Сумма операции : <%= op.getAmount() %>
            </li> <br />
            <% } %>
        </ul>


    <% }
    } catch (Exception e) {
        out.println("При выполнении возникла ошибка: " + e.getMessage());
    }
    %>
    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
</body>

</html>