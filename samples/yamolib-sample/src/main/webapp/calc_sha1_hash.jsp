<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.samples.client.Settings" %>
<%@ page import="ru.yandex.money.api.notifications.NotificationUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.google.common.collect.Maps" %>
<%@ page import="com.google.common.base.Function" %>
<%@ page import="com.google.common.base.Predicates" %>
<%--
  User: OneHalf
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, String[]> parameterMap = request.getParameterMap();

    Map<String, String> map = Maps.filterValues(Maps.transformValues(parameterMap, new Function<String[], String>() {
        @Override
        public String apply(String[] input) {
            if (input == null || input.length == 0) {
                return null;
            }
            return input[0];
        }
    }), Predicates.notNull());

    NotificationUtils notificationUtils = new NotificationUtils();
    out.print(notificationUtils.calculateHash(map, Settings.NOTIFICATION_SECRET));
%>