<%@ page import="ru.yandex.money.api.notifications.IncomingTransfer" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.google.common.collect.Iterables.limit" %>
<%@ page import="static com.google.common.collect.Lists.reverse" %>
<%@ page import="static com.samples.server.SampleIncomingTransferListener.getTransferList" %>
<%@ page import="static com.samples.server.SampleIncomingTransferListener.getTestTransferList" %>
<%@ page import="com.samples.client.Settings" %>
<%--
  User: OneHalf
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <script src="http://yandex.st/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
    <title>Полученные нотификации</title>
</head>
<body>
<div id="main">
    <h3 id="header">Последние полученные нотификации</h3>

    Ниже представлены данные по последним http-нотфикациям, полученным от Яндекс.Деньги.
    Настройки нотификаций выполняются <a href="https://sp-money.yandex.ru/myservices/online.xml">здесь</a>.
    Документация по http-уведомлениям <a href="http://api.yandex.ru/money/doc/dg/reference/notification-p2p-incoming.xml" >здесь</a>

    <h4>Последние нотификации полученные без тестового признака</h4>
    <p class="output">
        <% addNotificationsData(out, getTransferList()); %>
    </p>

    <h4>Последние тестовые нотификации</h4>
    <p class="output">
        <% addNotificationsData(out, getTestTransferList()); %>
    </p>

    <h4>Отправить нотификацию</h4>
    <p>Текущий секрет нотификаций: <%= Settings.NOTIFICATION_SECRET %></p>
    <p>Вы можете сымитировать запрос от Яндекс.Денег, воспользовавшись следующей формой:</p>
    <form id="notification_form" action="<%= Settings.NOTIFICATION_URI %>" method="get" target="_blank" onsubmit="onSubmitNotificationForm()">
        <table>
            <tbody>
                <% printTableTextParamRow("Тип операции", "notification_type", "p2p-incoming", session, request, out); %>
                <% printTableTextParamRow("Идентификатор операции", "operation_id", "818163584552108017", session, request, out); %>
                <% printTableParamRow("Сумма операции", "amount", "2.23", "type='number' step='0.01'", session, request, out); %>
                <% printTableParamRow("Валюта", "currency", "643", "type='text' ", session, request, out); %>
                <% printTableParamRow("Дата проведения операции", "datetime", "2012-12-17T17:49:52Z", "type='datatime'", session, request, out); %>
                <% printTableParamRow("Номер счета отправителя", "sender", "410011608243693", "type='number' ", session, request, out); %>
                <% printTableBooleanParamRow("Был ли использован код протекции", "codepro", false, out, session, request); %>
                <% printTableTextParamRow("Метка платежа", "label", "12625", session, request, out); %>
                <tr>
                    <td><label for=sha1_hash> Хэш для нотификации </label></td>
                    <td>
                        <input type="text" id="sha1_hash" name="sha1_hash"
                               value="<%= getParameterForNotification(session, request, "sha1_hash", "b9d4dee98caec486a8a3b1a577fce7efd0e7f0fb") %>"/>
                        <input type="button" name="calc_sha1_hash" value="Посчитать" onclick="calcSha1Hash()"/>
                    </td>

                </tr>
                <% printTableBooleanParamRow("Является ли нотификация тестовой", "test_notification", false, out, session, request); %>
                <tr>
                    <td colspan="2">
                        <input type="submit" name="send" value="Открыть в новой вкладке url нотификации"/>
                        <input type="button" name="asyncSend"
                               value="Отправить нотификацию асинхронно" onclick="onAsyncSubmitNotificationForm()"/>
                    </td>
                </tr>
            </tbody>
        </table>
    </form>
    <script type="text/javascript">

        function saveParamsToSession($notificationform) {
            $.ajax({
                // Костыль для сохранения параметров в сессию. Вызываем эту же страницу
                url: "${pageContext.request.contextPath}/notifications.jsp",
                type: "post",
                data: $notificationform.serialize(),
                dataType: "html"
            });
        }
        function onAsyncSubmitNotificationForm() {
            var $notificationform = $('#notification_form');
            saveParamsToSession($notificationform);
            $.ajax({
                url: "<%= Settings.NOTIFICATION_URI %>",
                type: "post",
                data: $notificationform.serialize(),
                dataType: "html",
                success: location.reload()
            });
        }

        function onSubmitNotificationForm() {
            saveParamsToSession($('#notification_form'));
            setTimeout(function(){
                location.reload()
            }, 1000);
        }

        function calcSha1Hash() {
            // setup some local variables
            var $form = $('#notification_form');
            // serialize the data in the form
            var serializedData = $form.serialize();

            $.ajax({
                url: "${pageContext.request.contextPath}/calc_sha1_hash.jsp",
                type: "post",
                data: serializedData,
                dataType: "html",
                success: function (data) {
                    $('#sha1_hash').val(data.trim())
                }
            });
        }
    </script>
</div>

</body>

</html>
<%!
    private void printTableTextParamRow(final String description, final String parameterName, final String defaultValue,
                                        HttpSession session, HttpServletRequest request, JspWriter out) throws IOException {
        printTableParamRow(description, parameterName, defaultValue, "type='text'", session, request, out);
    }

    private void printTableParamRow(final String description, final String parameterName, final String defaultValue, final String inpputAttribs, HttpSession session, HttpServletRequest request, JspWriter out) throws IOException {
        out.print("<tr>");
        out.print("<td><label for='" + parameterName + "'> " + description + "</label></td>");
        out.print("<td><input id='" + parameterName + "' " + inpputAttribs + " name='" + parameterName
                + "' value='" + getParameterForNotification(session, request, parameterName, defaultValue) + "'/></td>");
        out.print("</tr>");
    }

    private String getParameterForNotification(HttpSession session, HttpServletRequest request, String paramName, String defaultValue) {
        String attrName = "notification." + paramName;
        String parameter = request.getParameter(paramName);
        if (parameter != null) {
            session.setAttribute(attrName, parameter);
            return parameter;
        }
        parameter = (String) session.getAttribute(attrName);
        if (parameter != null) {
            return parameter;
        }
        return defaultValue;
    }

    private void addNotificationsData(JspWriter out, List<IncomingTransfer> transferMap) throws IOException {
        if (transferMap.isEmpty()) {
            out.print("<p>Еще не было нотификаций с момента старта приложения</p>");
            return;
        }

        for (IncomingTransfer incomingTransfer : limit(reverse(transferMap), 5)) {
            out.print("<p>");
            out.print(incomingTransfer.toString());
            out.print("</p>");
        }
    }

    private void printTableBooleanParamRow(final String description, String paramName, boolean defaultValue, JspWriter out, HttpSession session,
                                           HttpServletRequest request) throws IOException {
        out.print("<tr>");
        out.print(String.format("<td><label for='%s'>%s</label></td>", paramName, description));
        out.print(String.format("<td><select id='%s' name='%s'>", paramName, paramName));

        boolean value = Boolean.parseBoolean(getParameterForNotification(session, request, paramName, String.valueOf(defaultValue)));
        String selectedAttr = " selected='selected'";

        out.print("<option value='false'" + (value ? "" : selectedAttr) + ">false</option>");
        out.print("<option value='true'" + (value ? selectedAttr : "") + ">true</option></select></td>");
        out.print("</tr>");
    }

%>