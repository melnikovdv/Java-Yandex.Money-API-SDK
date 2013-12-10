##yamolib - java-библиотека API Яндекс.Денег
==========================================

В библиотеке реализованы вызовы следующих функций API Яндекс.Денег: 

* прохождение OAuth-аутентификации, 
* информация о счете, 
* история операций и детальная информация по ним, 
* переводы денег другим пользователям, 
* оплата в магазины. 

Библиотека представляет собой package `ru.yandex.money.api`, который содержит:

* программные интерфейсы `ApiCommandsFacade`, `TokenRequester` и, объединяющий оба эти интерфейса, `YandexMoney`;
* реализации интерфейсов `ApiCommandsFacadeImpl`, `TokenRequesterImpl` и `YandexMoneyImpl`;
* классы-перечисления;
* вспомогательные классы (response-объекты вывода результатов запросов к API);
* исключения.

*Внимание:* использует библиотеки Gson (http://sites.google.com/site/gson/) для работы с json и  Apache HTTP client версии 4 (http://hc.apache.org/httpcomponents-client-ga/index.html). Библиотека разрабатывалась и запускалась на Java 6.

### Основные методы интерфейса TokenRequester
--------------------------------------

* `authorizeUri` — метод для получения URI, по которому нужно переидти для инициации OAuth-авторизации.
На вход принимает список прав доступа и URI редиректа после авторизации.
Возвращает URI OAuth-авторизации.

* `receiveOAuthToken` — метод для обмена временного кода, полученного от сервера Яндекс.Денег после вызова метода authorize, на постоянный токен доступа к счету пользователя. 
На вход принимает временный код.
Возвращает экземпляр класса ReceiveOAuthTokenResponse, содержащий токен или ошибку.

### Основные методы интерфейса ApiCommandsFacade
--------------------------------------

* `accountInfo` — метод для получения информации о счете пользователя.
На вход принимает токен пользователя.
Возвращает экземпляр класса AccountInfoResponse, который содержит поля: номер счета, баланс, валюта счета.

* `operationHistory` — метод для получения истории операций пользователя. 
На вход принимает токен пользователя, а также комбинации из следующих парметров:
** номер первой записи (постраничный вывод)
** количество операций и тип операций (приход или расход)
** даты начала/окончания периода, в рамках которого нужно просмотреть историю
** метку, по которой нужно найти платеж
** признак необходимости запроса истории сразу с деталями платежей
Возвращает экземпляр класса operationHistoryResponse, который содержит код ошибки, если таковая произошла, номер записи следующей страницы, если таковая есть (постраничный вывод) и коллекцию операций. Операции представляют собой объект Operation (сумма, время, и комментарии к операции).

* `operationDetail` — метод для получения детальной информации по операции из истории или платежей.
На вход принимает токен пользователя и идентификатор операции.
Возвращает экземпляр класса OperationDetailResponse, который унаследован от объекта Operation и предоставляет расширенную информацию по платежу/зачислению.

* `requestPaymentP2P` — метод перевода средств на счет другого пользователя. 
На вход принимает токен пользователя, номер счета (или привязанного телефона) получателя, сумму и комментарий.
Возврашает экземпляр класса RequestPaymentResponse, который содержит ошибку, если таковая произошла, информацию о статусе операции, идентификатор операции, контракт и баланс.

* `requestPaymentShop` — метод оплаты в магазины. 
На вход принимает токен пользователя и Map параметров магазина.
Возврашает экземпляр класса RequestPaymentResponse, который содержит ошибку, если таковая произошла, информацию о статусе операции, идентификатор операции, контракт и баланс.

* `requestPaymentToPhone` — метод оплаты на счета мобильных операторов.
На вход принимает токен пользователя, номер телефона и сумму пополнения.
Возврашает экземпляр класса RequestPaymentResponse, который содержит ошибку, если таковая произошла, информацию о статусе операции, идентификатор операции, контракт и баланс.

* `processPayment` — метод для подтверждения перевода, полученного при вызове requestPaymentP2P, requestPaymentToPhone или requestPaymentShop.
На вход принимает идентификатор запроса.
Возвращает экземпляр класса ProcessPaymentResponse, который содержит информацию о статусе платежа, балнесе после проведения операции и идентификатор платежа.

* `revokeOAuthToken` — метод для отзыва токена.
На вход принимает токен, подлежещий удалению.
Не возвращает никаких данных, при ошибках будет порождено одно из соответствующих исключений. Например, InvalidTokenException, если токен не существует, или уже был отозван.

Примеры использования
---------------------

Для выполнения операций со счетом через API необходимо получить разрешение пользователя, то есть токен. Его можно получить следующими вызовами (к примеру, с доступом на просмотр информации о счете и истории операций):

      TokenRequester tokenRequester = new TokenRequesterImpl(Settings.CLIENT_ID, httpClient);

      // Указываем необходимые для работы приложения права 
      // на доступ к счету пользователя
      Collection<Permission> scope = new LinkedList<Permission>();
      scope.add(new AccountInfo());
      scope.add(new OperationHistory());   
  
      codeReqUri = tokenRequester.authorizeUri(scope, Settings.REDIRECT_URI);
      response.sendRedirect(codeReqUri);

Затем на странице редиректа выполняем обмен временного кода на постоянный токен доступа

      String code = request.getParameter("code");
      ReceiveOAuthTokenResponse resp = tokenRequester.receiveOAuthToken(code, Settings.REDIRECT_URI);
      if (resp.isSuccess()) {
        out.println("Токен: " + resp.getAccessToken());
      }

При создании объекта tokenRequester ему передается идентификатор приложения, который обычно прописывается в настройках приложения (`settings.properties` в наших примерах). Затем проставляем права scope и делаем вызов полученного URI.

#### account-info
Чтобы получить информацию о счете пользователя, можно таким же образом создать объект, а затем вызвать метод, передав ему токен пользователя:

      ApiCommandsFacade apiCommandsFacade = new ApiCommandsFacadeImpl(httpClient);
      String token = (String) session.getAttribute("token");      
      try {
        AccountInfoResponse resp = apiCommandsFacade.accountInfo(token);
        out.println("Счет: " + resp.getAccount()) ;
        out.println("Баланс: " + resp.getBalance()) ;
        out.println("Валюта: " + resp.getCurrency()) ;
        out.println("Счет идентифицирован: " + resp.isIdentified()) ;
        out.println("Тип счета: " + resp.getAccountType()) ;
      } catch (Exception e) {
        out.println("При выполнении возникла ошибка: " + e.getMessage());
      }

Информация о счете получена.

#### operation-history
Получение вместе с деталями первых пяти входящих платежей за последнюю неделю, выполненных с меткой "На подарок Васе Пупкину":

      Date lastWeek = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
      OperationHistoryResponse history = apiCommandsFacade.operationHistory(
          token, 0, 5, EnumSet.of(OperationHistoryType.deposition), true, lastWeek, null, "На подарок Васе Пупкину");

#### operation-details
Запрос деатлей платежа по его идентификатору. `operationId` может быть получен из результата processPayment, из operation-history или из http-нотификации:

      OperationDetailsResponse details = apiCommandsFacade.operationDetail(token, operationId);

#### request-payment/process-payment
Пример p2p перевода с привязанной карты:

        RequestPaymentResponse requestPaymentResponse = apiCommandsFacade.requestPaymentP2P(
                token, "410011234567890", BigDecimal.TEN, "comment", "message");

        if (!requestPaymentResponse.isSuccess()) {
            System.out.println("Ошибка выполнения платежа: " + requestPaymentResponse);
            return;
        }

        if (!requestPaymentResponse.isPaymentMethodAvailable(MoneySource.card)) {
            System.out.println("Карты нет в числе допустимого метода платежа: " + processPaymentResponse);
            return;
        }

        String cardSecurityCode = "000";
        ProcessPaymentResponse processPaymentResponse = apiCommandsFacade.processPaymentByCard(
                token, requestPaymentResponse.getRequestId(), cardSecurityCode);

        if (!processPaymentResponse.isSuccess()) {
            System.out.println("Ошибка выполнения платежа: " + processPaymentResponse);
            return;
        }


Примерно так же обстоят дела и с другими вызовами. Предлагаем вам посмотреть (а может, и запустить) более полные примеры использования библиотеки (`war/ym.war` в архиве с исходниками или на github'е https://github.com/melnikovdv/Java-Yandex.Money-API-SDK). Среди них вы найдете пример оплаты за мобильную связь и небольшой бонус, который позволяет без исопльзования API создать прямую ссылку для перевода денег на другой счет.

#### Тестовые платежи
Отличается создание объекта с командами:

        CommandUrlHolder urlHolder = new TestUrlHolder();
        ApiCommandsFacade apiCommandsFacade = new ApiCommandsFacadeImpl(httpClient, urlHolder);

Далее перед вызовом команды выставляется ожидаемое поведение:

        urlHolder.setTestCard("available");        // означает, что система должа показывать, что у пользователя карта привязана
        urlHolder.setTestPayment(true);            // переводит запросы в боевой/тестовый режим
        urlHolder.setTestResult(RequestPaymentError.ILLEGAL_PARAMS); // Сервер Яндекс.Деньги будет возвращать указанную ошибку. при запросах платежа. Код ошибки должен быть из списка реально возможных кодов для вызываемой команды

        RequestPaymentResponse response = apiCommandsFacade.requestPaymentShop(token, "335", Collections.<String,String>emptyMap());
        System.out.println(response); // Распечатает ответ с ошибкой illegal_params

Для успешного запуска примеров из комплекта следует проделать следующее:

* установить (если ранее не был установлен) какой-нибудь application server, например Apache Tomcat (http://tomcat.apache.org/);
* задеплоить веб-архив yamolib-sample.war (в каталоге yamolib-sample/target/).

Или собрать проект самостоятельно. Для этого:

* зарегистрировать приложение, т.е. получить идентификатор клиента (https://sp-money.yandex.ru/myservices/new.xml) и прописать его в настройки примеров (settings.properties);
* изменить `REDIRECT_URI` (`settings.properties`), если отличается;
* скомпилировать и запустить веб-приложение.

#### Пример сборки maven war файла:

git clone git@github.com:melnikovdv/Java-Yandex.Money-API-SDK.git  
cd Java-Yandex.Money-API-SDK  
mvn clean package  

Ссылка администирования приложений: https://sp-money.yandex.ru/myservices/admin.xml  
Ссылка для управление доступом приложений: https://sp-money.yandex.ru/myservices/
