<%@ page import="org.apache.http.NameValuePair" %>
<%@ page import="org.apache.http.client.methods.HttpGet" %>
<%@ page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="org.apache.http.client.utils.URLEncodedUtils" %>
<%@ page import="org.apache.http.message.BasicNameValuePair" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
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
    <title>Простой перевод</title>
</head>
<body>
<%!
    private final static String PAR_DEST = "destination";
    private final static String PAR_SUM = "sum";
    private final static String PAR_TITLE = "title";
    private final static String PAR_MESS = "message";
    private final static String PAR_SHORT_MESS = "short-dest";

    private final static String DIRECT_PAYMENT_URI =
            "https://money.yandex.ru/direct-payment.xml?" +
                    "isDirectPaymentFormSubmit=true&" +
                    "ErrorTemplate=ym2xmlerror&" +
                    "ShowCaseID=7&" +
                    "SuccessTemplate=ym2xmlsuccess&" +
                    "isViaWeb=true&js=0&" +
                    "p2payment=1&" +
                    "rnd=595587893&" +
                    "scid=767&" +
                    "secureparam5=5&" +
                    "shn=ShowcaseName&" +
                    "showcase_comm=0.5%25&" +
                    "suspendedPaymentsAllowed=true&" +
                    "targetcurrency=643&";
%>

<div id="main">
    <h3 id="header">Простой перевод</h3>
    <%
        request.setCharacterEncoding("UTF-8");
        if (request.getMethod().equals(HttpPost.METHOD_NAME)) {
            String dest = String.valueOf(request.getParameter(PAR_DEST));
            String sum = request.getParameter(PAR_SUM);
            String title = request.getParameter(PAR_TITLE);
            String mess = request.getParameter(PAR_MESS);
            String shortMess = request.getParameter(PAR_SHORT_MESS);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("FormComment", title));
            params.add(new BasicNameValuePair("destination", mess));
            params.add(new BasicNameValuePair("short-dest", shortMess));
            params.add(new BasicNameValuePair("receiver", dest));
            params.add(new BasicNameValuePair("sum", sum));

            response.sendRedirect(DIRECT_PAYMENT_URI +
                    URLEncodedUtils.format(params, "UTF-8"));
        }
    %>

    <% if (request.getMethod().equals(HttpGet.METHOD_NAME)) { %>
    <div>
        Для создания простой платежной ссылки запоните следующие поля и нажмите
        кнопку "Отправить".
        <form method="POST">
            <div id="inputs">
                <p>
                    Номер счета или эккаунт на яндексе: <br/>
                    <input name="<%= PAR_DEST %>" type="text"
                           placeholder="mdv00 или 41001901291751" size="40"/>
                </p>

                <p>
                    Сумма: <br/>
                    <input name="<%= PAR_SUM %>" type="text" placeholder="1.50"
                           size="40"/>
                </p>

                <p>
                    Заголовок платежа: <br/>
                    <input name="<%= PAR_TITLE %>" type="text"
                           placeholder="заголовок перевода для получателя"
                           size="40"/>
                </p>

                <p>
                    Сообщение для получателя: <br/>
                    <input name="<%= PAR_MESS %>" type="text"
                           placeholder="сообщение для получателя" size="40"/>
                </p>

                <p>
                    Комментарий к платежу: <br/>
                    <input name="<%= PAR_SHORT_MESS %>" type="text"
                           placeholder="комментарий к платежу" size="40"/>
                </p>
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