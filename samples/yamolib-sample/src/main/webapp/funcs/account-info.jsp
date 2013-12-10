<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.YandexMoney" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.response.AccountInfoResponse" %>
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
    <h3 id="header">Пример вызова API-функции account-info</h3>
    <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/funcs/account-info.jsp">тут</a>.
        Результат выполнения:
    </p>
    <%
        YandexMoney ym = new YandexMoneyImpl(Settings.CLIENT_ID);
        String token = (String) session.getAttribute("token");
        AccountInfoResponse resp = null;
        try {
            resp = ym.accountInfo(token);
        } catch (Exception e) {
            out.println("При выполнении возникла ошибка: " + e.getMessage());
        }

        if (resp != null) {
    %>
    <p class="code">
        Счет: <%= resp.getAccount() %> <br/>
        Баланс: <%= resp.getBalance() %> <br/>
        Валюта: <%= resp.getCurrency() %> <br/>
    </p>
    <% } %>
    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
</body>

</html>