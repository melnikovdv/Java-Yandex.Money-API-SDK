<%@ page import="client.Consts" %>
<%@ page import="ru.yandex.money.api.InsufficientScopeException" %>
<%@ page import="ru.yandex.money.api.InternalServerErrorException" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.response.ReceiveOAuthTokenResponse" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <title>Cтраница OAuth-редиректа</title>
</head>
<body>
<div id="main">
    <h3 id="header">Cтраница OAuth-редиректа</h3>
    <%
        String code = request.getParameter("code");
        if (code == null) {
            response.sendRedirect("auth.jsp");
        }
    %>

    Перед тем, как оказаться на этой странице редиректа, у нас был вызов страницы
    auth.jsp (<a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/web/auth.jsp">исходник</a>)
    Затем сервер Яндекс.Денег вернул нас на эту станицу - redirected.jsp
    (<a href="https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/web/redirected.jsp">исходник</a>),
    и мы получили временный код в get-параметрах:

    <p class="code">Временный код: <%= code.substring(1, 20).concat("...") %>
    </p>

    Затем меняем его на постоянный токен. Результат выполнения:
    <%
        YandexMoneyImpl ym = new YandexMoneyImpl(Consts.CLIENT_ID);
        try {
            ReceiveOAuthTokenResponse resp =
                    ym.receiveOAuthToken(code, Consts.REDIRECT_URI);
            if (resp.isSuccess()) {
                session.setAttribute("token", resp.getAccessToken());
    %>
    <p class="code">Наш токен: <%= resp.getAccessToken().substring(1, 30).concat("...")  %>
    </p>

    <p>
        После получения токена можно осуществлять вызовы всех остальных функций.
        Приведем примеры подобных вызовов:
    </p>
    <ul>
        <li><a href="funcs/account-info.jsp">account-info</a></li>
        <li><a href="funcs/operation-history.jsp">operation-history</a></li>
        <li><a href="funcs/operation-details.jsp">operation-details</a></li>
        <li><a href="funcs/request-paymentp2p.jsp">request-payment p2p</a></li>
    </ul>
    <p>
        А здесь покажем реальные юзкейсы:
    </p>
    <ul>
        <li>Как оплатить мобильный телефон через API <a href="mobile/">смотрим
            тут</a></li>
        <li>Как сделать форму "а-ля" donation или простой перевод без API <a
                href="simple/">смотрим тут</a></li>
    </ul>
    <% } else { %>
    <p>Cлучилась ошибка авторизации. Проверьте, что правильно установили
        константы</p>
    В ответе получена ошибка: <%= resp.getError() %>
    <div class="tomainpage"><a href="index.jsp">на главную</a></div>
    <% } %>
    <%
        } catch (InternalServerErrorException e) {
            out.println("Внутренняя ошибка Яндекс.Денег, попробуйте позже: " + e
                    .getMessage());
        } catch (InsufficientScopeException e) {
            out.println("Недостаточно прав: " + e.getMessage());
        }
    %>
</div>

</body>

</html>