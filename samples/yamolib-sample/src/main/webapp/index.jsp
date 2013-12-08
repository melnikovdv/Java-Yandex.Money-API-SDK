<%@ page import="com.samples.client.Settings" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <title>Примеры использования Yandex.Money API</title>
</head>
<body>
<div id="main">
    <h3 id="header">Примеры использования Yandex.Money API</h3>

    <p>
        Попробуем сделать несколько вызовов, показывающих как можно работать
        с Yandex.Money Api.
        По <a href="http://api.yandex.ru/money/">инструкции</a> первым делом
        нужно получить идентификатор приложения. Для этого нужно перейти по
        <a href="https://sp-money.yandex.ru/myservices/new.xml">ссылке</a>
        и ввести следующие данные (для случая локального запуска на Apache
        Tomcat с настройками по умолчанию):
    </p>
    <ul>
        <li>Название: любое, но помните, что его будут видеть пользователи при
            подтверждении прав на доступ к их деньгам
        </li>

        <li>Адрес сайта: http://localhost:8080/</li>
        <li>Redirect URI: <%= Settings.REDIRECT_URI %></li>
    </ul>
    Если вы запускаетесь не на локальном сервере, а на хостинге, то стоит
    заменить localhost
    на адрес вашего сайта и указать правильный путь к redirected.jsp.
    <p>
        <b>Внимание: </b> затем нужно полученный идентификатор клиента
        скопировать в файл client/settings.properties
        в свойство client_id. Далее нужно указать redirect_url такой же, как
        указали в сервисе при регистрации.
    </p>

    <p>
        После этого нам нужно запросить у какого-либо пользователя Яндекс.Денег
        (можно указать свой
        эккаунт) разрешения на доступ вашего приложения к его эккаунту. Это
        разрешение представляет собой
        токен доступа, который далее мы попробуем получить. Нажмите кнопку
        "Отправить запрос" для продолжения. <br/>
    </p>

    <form action="auth.jsp">
        <input type="submit" name="btnSendRequest" value="Отправить запрос"/>
    </form>

    <p>
        Примеры использования http-уведомлений можно посмотреть на странице
        <a href="notifications.jsp">notifications.jsp</a>
    </p>
</div>

</body>
</html>