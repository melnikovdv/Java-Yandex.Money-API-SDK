<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.YandexMoney" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.enums.MoneySource" %>
<%@ page import="ru.yandex.money.api.response.RequestPaymentResponse" %>
<%@ page import="java.math.BigDecimal" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../styles.css">
    <title>Пример вызова API-функции request-paymentP2P</title>
</head>
<body>
<div id="main">
    <h3 id="header">Пример вызова API-функции request-paymentP2P</h3>

    <%
        YandexMoney ym = new YandexMoneyImpl(Settings.CLIENT_ID);
        String token = (String) session.getAttribute("token");
        try {
            RequestPaymentResponse resp = ym.requestPaymentP2P(token,
                    "410011161616877", BigDecimal.valueOf(0.02),
                    "comment to payment", "payment message");
            if (resp.isSuccess()) {
    %>
    <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/funcs/operation-details.jsp">тут</a>.
        Результат выполнения:
    </p>
    <p class="code">
        Статус запроса платежа: <%= resp.getStatus() %> <br/>
        Доступные методы проведения платежа: <%= resp.getMoneySource() %>; <br/>
        Возможна ли оплата с привязанной карты: <%= resp
            .isPaymentMethodAvailable(
                    MoneySource.card) %> <br/>
        Возможна ли оплата из кошелька: <%= resp.isPaymentMethodAvailable(
            MoneySource.wallet) %> <br/>
        Идентификтаор платежа: <%= resp.getRequestId() %>; <br/>
        Контракт: <%= resp.getContract() %>; <br/>
        Текущий баланс: <%= resp.getBalance() %>; <br/>
    </p>

    <%
        session.setAttribute("requestId", resp.getRequestId());
    %>
    <p>
        Теперь, получив request_id можем провести этот платеж:
        <a href="process-payment.jsp">process-payment</a>
    </p>
    <%  } else
            out.println("В ответе получена ошибка: " + resp.getError());
        } catch (Exception e) {
            out.println("При выполнении возникла ошибка: " + e.getMessage());
        }
    %>
    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
</body>

</html>
