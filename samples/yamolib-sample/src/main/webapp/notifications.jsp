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
    Настройки нотификаций выполняются <a href="https://sp-money.yandex.ru/myservices/online.xml">здесь</a>

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
    <form id="notification_form" action="<%= Settings.NOTIFICATION_URI %>" method="get" target="_blank" onsubmit="onSubmitNotificationForm()">
        <table>
            <tbody>
                <tr>
                    <td><label for="notification_type"> Тип операции</label></td>
                    <td><input id="notification_type" type="text" name="notification_type" value="p2p-incoming"/></td>
                </tr>
                <tr>
                    <td><label for="operation_id"> Идентификатор операции </label></td>
                    <td><input type="text" id="operation_id" name="operation_id"
                           value="<%= getParameterForNotification(session, request, "operation_id", "818163584552108017") %>"/></td>
                </tr>
                <tr>
                    <td><label for="amount"> Сумма операции </label></td>
                    <td><input type="number" id="amount" name="amount" step="0.01"
                           value="<%= getParameterForNotification(session, request, "amount", "2.23") %>"/></td>
                </tr>
                <tr>
                    <td><label for="currency"> Валюта</label></td>
                    <td><input type="number" id="currency" name="currency" value="643"/></td>
                </tr>
                <tr>
                    <td><label for="datetime"> Дата проведения операции </label></td>
                    <td><input type="datetime" id="datetime" name="datetime"
                           value="<%= getParameterForNotification(session, request, "datetime", "2012-12-17T17:49:52Z") %>"/></td>
                </tr>
                <tr>
                    <td><label for="sender"> Номер отправителя </label></td>
                    <td><input type="text" id="sender" name="sender"
                           value="<%= getParameterForNotification(session, request, "sender", "410011608243693") %>"/></td>
                </tr>
                <tr>
                    <td><label for="codepro"> Был ли использован код протекции </label></td>
                    <td><select id="codepro" name="codepro">
                        <option value="false">false</option>
                        <option value="true">true</option></select></td>

                </tr>
                <tr>
                    <td><label for="label"> Метка платежа </label></td>
                    <td><input type="text" id="label" name="label"
                           value="<%= getParameterForNotification(session, request, "label", "12625") %>"/></td>
                </tr>
                <tr>
                    <td><label for=sha1_hash> Хэш для нотификации </label></td>
                    <td>
                        <input type="text" id="sha1_hash" name="sha1_hash"
                               value="<%= getParameterForNotification(session, request, "sha1_hash", "b9d4dee98caec486a8a3b1a577fce7efd0e7f0fb") %>"/>
                        <input type="button" name="calc_sha1_hash" value="Посчитать" onclick="calcSha1Hash()"/>
                    </td>

                </tr>
                <tr>
                    <td><label for="test_notification"> Является ли нотификация тестовой </label></td>
                    <td><select id="test_notification" name="test_notification" >
                        <option value="true">true</option>
                        <option value="false">false</option></select></td>
                </tr>
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
            saveParamsToSession($('#notification_form'))
            setTimeout(function(){
                location.reload()
            }, 1000);
        }

        function calcSha1Hash() {
            // setup some local variables
            var $form = $('#notification_form');
            // serialize the data in the form
            var serializedData = $form.serialize();

            var request = $.ajax({
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
%>