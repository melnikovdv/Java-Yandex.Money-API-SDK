<%@ page import="com.samples.client.Settings" %>
<%@ page import="org.apache.http.client.methods.HttpGet" %>
<%@ page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.enums.MoneySource" %>
<%@ page import="ru.yandex.money.api.enums.Status" %>
<%@ page import="ru.yandex.money.api.response.RequestPaymentResponse" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
         language="java" %>
<html>
<head>
    <script src="http://yandex.st/jquery/1.6.4/jquery.min.js"
            type="text/javascript"></script>
    <script src="https://raw.github.com/kugaevsky/joined_inputs/master/join_inputs.jquery.min.js"
            type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="../styles.css">
    <title>Оплата мобильного телефона при помощи API Янекс.Денег</title>
</head>
<body>
<%!
    private static final String OPERATOR = "operator";
    private static final String OPERATOR_MEGAFON = "megafon";
    private static final String OPERATOR_MTS = "mts";
    private static final String OPERATOR_BEELINE = "beeline";
    private static final String OPERATOR_TELE2 = "tele2";
    private static final String NUMBER_CODE = "code";
    private static final String NUMBER = "number";
    private static final String SUM = "sum";
%>

<div id="main">
    <h3 id="header">Оплата мобильного телефона при помощи API Янекс.Денег</h3>
    <%
        request.setCharacterEncoding("UTF-8");
        if (request.getMethod().equals(HttpPost.METHOD_NAME)) {
            String operator = request.getParameter(OPERATOR);
            String sum = request.getParameter(SUM);
            String code = request.getParameter(NUMBER_CODE);
            String number = request.getParameter(NUMBER);
            String patternId = "337";

            if (operator.equals(OPERATOR_MEGAFON))
                patternId = "337";
            if (operator.equals(OPERATOR_MTS))
                patternId = "335";
            if (operator.equals(OPERATOR_BEELINE))
                patternId = "343";
            if (operator.equals(OPERATOR_TELE2))
                patternId = "928";

            Map<String, String> map = new HashMap<String, String>();
            map.put("PROPERTY1", code);
            map.put("PROPERTY2", number);
            map.put(SUM, sum);

            YandexMoneyImpl ym = new YandexMoneyImpl(Settings.CLIENT_ID);
            String token = (String) session.getAttribute("token");
            try {
                RequestPaymentResponse resp = ym.requestPaymentShop(token,
                        patternId, map);

                if (resp.isSuccess()) {
    %>
                    <p>Исходный код страницы <a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/samples/yamolib-sample/src/main/webapp/mobile/index.jsp">тут</a>.
                        Результат выполнения:
                    </p>
                    <p class="code">
                        Статус запроса платежа: <%= resp.getStatus() %> <br />
                        Код ошибки при проведении платежа: <%= resp.getError() %>; <br />
                        Доступные методы проведения платежа: <%= resp.getMoneySource() %>; <br />
                        Возможна ли оплата с привязанной карты: <%= resp.isPaymentMethodAvailable(
                            MoneySource.card) %> <br />
                        Возможна ли оплата из кошелька: <%= resp.isPaymentMethodAvailable(
                            MoneySource.wallet) %> <br />
                        Идентификтаор платежа: <%= resp.getRequestId() %>; <br />
                        Контракт: <%= resp.getContract() %>; <br />
                        Текущий баланс: <%= resp.getBalance() %>; <br />
                    </p>

    <%
                    if (resp.getStatus() == Status.success) {
                        session.setAttribute("requestId", resp.getRequestId());
    %>
                        <p>
                            Теперь, получив request_id можем провести этот платеж:
                            <a href="../funcs/process-payment.jsp">process-payment</a>
                        </p>
    <%              }
                } else
                    out.println("В ответе получена ошибка: " + resp.getError());
            } catch (Exception e) {
                out.println("При выполнении возникла ошибка: " + e.getMessage());
            }
        }
    %>

    <% response.setCharacterEncoding("UTF-8");
        if (request.getMethod().equals(HttpGet.METHOD_NAME)) { %>
    <div>
        <form method="POST">
            <div>
                <p>
                    Оператор: <br/>
                    <input id="<%= OPERATOR_MEGAFON %>" name="<%= OPERATOR %>" type="radio" checked="true" value="<%= OPERATOR_MEGAFON %>" />
                    <label for="<%= OPERATOR_MEGAFON %>">Мегафон</label>
                    <br />
                    <input id="<%= OPERATOR_MTS %>" name="<%= OPERATOR %>" type="radio" value="<%= OPERATOR_MTS %>"/>
                    <label for="<%= OPERATOR_MTS %>">МТС</label>
                    <br />
                    <input id="<%= OPERATOR_BEELINE %>" name="<%= OPERATOR %>" type="radio" value="<%= OPERATOR_BEELINE %>"/>
                    <label for="<%= OPERATOR_BEELINE %>">Билайн</label>
                    <br />
                    <input id="<%= OPERATOR_TELE2 %>" name="<%= OPERATOR %>" type="radio" value="<%= OPERATOR_TELE2 %>"/>
                    <label for="<%= OPERATOR_TELE2 %>">Теле2</label>
                </p>
                <div id="inputs">
                    <p>
                        Номер
                        <input name="<%= NUMBER_CODE %>" type="text" placeholder="921" size="3" />
                        <input name="<%= NUMBER %>" type="text" placeholder="3020052" size="7" />
                    </p>
                    <p>
                        Сумма
                        <input name="<%= SUM %>" type="text" placeholder="1.50" size="5" />
                    </p>
                </div>

            </div>
            <input type="submit" value="Отправить"/>
        </form>
    </div>
    <% } %>

    <div class="tomainpage"><a href="javascript:history.back()">назад</a></div>
</div>
<script type="text/javascript">
    $('#inputs input').joinInputs()
</script>
</body>

</html>
