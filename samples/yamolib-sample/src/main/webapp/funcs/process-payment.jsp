<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.response.ProcessPaymentResponse" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../styles.css">
    <title>Пример вызова API-функции process-payment</title>
</head>
<body>
<div id="main">
    <h3 id="header">Пример вызова API-функции process-payment</h3>

    <%
        ru.yandex.money.api.YandexMoney ym = new YandexMoneyImpl(Settings.CLIENT_ID);
        String token = (String) session.getAttribute("token");
        String requestId = (String) session.getAttribute("requestId");
        try {
            ProcessPaymentResponse resp = ym.processPaymentByWallet(token,
                    requestId);
            if (resp.isSuccess()) {
    %>
                <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/funcs/process-payment.jsp">тут</a>.
                    Результат выполнения:
                </p>
                <p class="code">
                    Результат ваполнения операции: <%= resp.getStatus() %> <br />
                    Идентификатор платежа: <%= resp.getPaymentId() %> <br />
                    Баланс после проведения: <%= resp.getBalance() %> <br />
                    Счет плательщика: <%= resp.getPayer() %> <br />
                    Счет получателя: <%= resp.getPayee() %> <br />
                    Сумма поступившая на счет получателя: <%= resp.getCreditAmount() %> <br />
                </p>
    <%       } else
                out.println("В ответе получена ошибка: " + resp.getError());
        } catch (Exception e) {
            out.println("При выполнении возникла ошибка: " + e.getMessage());
        }
    %>
    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
</body>

</html>