<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.YandexMoney" %>
<%@ page import="ru.yandex.money.api.YandexMoneyImpl" %>
<%@ page import="ru.yandex.money.api.enums.Destination" %>
<%@ page import="ru.yandex.money.api.rights.*" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.LinkedList" %>
<%--
  User: dvmelnikov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Yandex.Money API OAuth request</title></head>
<body>
Yandex.Money API OAuth request...
<%
    YandexMoney ym = new YandexMoneyImpl(Settings.CLIENT_ID);

    Collection<Permission> scope = new LinkedList<Permission>();
    scope.add(new AccountInfo());
    scope.add(new OperationHistory());
    scope.add(new OperationDetails());
    scope.add(new MoneySource(true, true));
    scope.add(new PaymentP2P().limit(30, "1000"));
//    scope.add(new PaymentShop().limit(1, "100"));
    scope.add(new Payment(Destination.toPattern, "337", 1, "100"));
    scope.add(new Payment(Destination.toPattern, "335", 1, "100"));
    scope.add(new Payment(Destination.toPattern, "343", 1, "100"));
    scope.add(new Payment(Destination.toPattern, "928", 1, "100"));

    response.sendRedirect(ym.authorizeUri(scope, Settings.REDIRECT_URI, false));
%>
</body>
</html>