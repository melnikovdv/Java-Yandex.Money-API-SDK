## yamodroid - android-библиотека для работы с API Яндекс.Денег

* * *

## Описание

Библиотека позволяет использовать возможности [API Яндекс.Денег](http://api.yandex.ru/money/) в вашем android-приложении.  
Она работает на основе библиотеки [yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamolib).
Прежде, чем работать с библиотекой yamodroid посмотрите описание библиотеки [yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/yamolib/readme.md). 

### Возможности

* OAuth-авторизация и получение токена доступа
* Запрос истории операций
* Запрос детальной информации об операции
* Перевод денежных средств на другие счета (p2p)
* Оплата в магазины

Для всех указанных возможностей реализованы готовые к использованию формы, поэтому для 
внедрения в приложение требуется минимум усилий. 

Для работы создан специальный класс YandexMoneyDroid, который позволяет просто и удобно делать запросы. 
Все перечисленные выше возможности - это Activity, тр есть их можно запустить с помощью обычного Intent'а и 
обработать результат с помощью onActivityResult. 

Если же вы все же решите содавать и стартовать интенты вручную, то для удобной передачи параметров в 
Activities библиотеки предусмотрен специальный объект `IntentCreator`, который облегчит создание 
intent'а и передачу в него параметров.

**Системные требования**: Android SDK версии 7 и транзитивные требования библиотеки 
[yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamolib).
   
**На заметку**: 
*Если вы хотите создать свои формы или использовать некоторые возможности API в фоне, ничего
не мешает вызывать методы библиотеки [yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamolib) напрямую.
Только следует помнить, что все этим операции обращаются к сети и делать вызовы следует в отдельном от UI потоке.*

## Установка и настройка

Структуру проектов, changelog и ссылки на инструменты для сборки можно посмотреть в 
[описании проекта](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/readme.md). 

Этот проект - это android-библиотека, а это значит, что она содержит ресурсы, манифест и прочее 
специфичное для платформы android. Их нужно правильным способом добавить в свое приложение.

#### Первый и самый простой способ

Если вы используете Maven в своих проектах, то просто установите библиотеку в ваш локальный Maven-репозиторий 
(команда `mvn install`) и подключите `dependency` к своему android-приложению. Затем обновите при помощи Maven файлы 
среды разработки вашего проекта (например, mvn idea:idea). И не забываем прописать все Activity из Manifest'а.

#### Второй способ

Если вы не используете Maven в своих проектах, то можете собрать проект командой `mvn package`. После этого у вас появится 
файл `target\yamodroid-x.x.x-SNAPSHOT.apklib`. Этот файл вы можете переименовать в .zip и затем разархивировать  
содержимое (ресурсы, манифест, исходники) в свое приложение.

#### Третий способ

Просто скопируйте себе файлы из каталогов src и res и скомпилируйте все вместе со своим проектом. Добавьте из 
Manifest-файла библиотеки все Activity в Manifest вашего приложения. 

Второй и третий способы не очень удобны для обновления библиотеки.

## Getting started guide

Покажем, как осуществлять вызовы функций библиотеки.

Первым делом нужно создать экземпляр главного класса библиотеки. В качестве параметра нужно передать 
идентификатор вашего приложения в API Яндекс.Денег, который вы получили при 
[регистрации](https://sp-money.yandex.ru/myservices/new.xml). 

```java
    YandexMoneyDroid ymd = new YandexMoneyDroid(YOUR_APP_CLIENT_ID);
```

### Авторизация 

Далее нужно получить токен доступа - разрешение пользоваться проводить операции с его счетом в Яндекс.Деньгах. 
Иницировать процесс авторизации нужно следующим способом:

```java
    ymd.authorize(yourAppActivity, AUTH_REQUEST_CODE, REDIRECT_URI, 
            Consts.getPermissions(), showResultDialog, dialogListener);
```

Объясним значения параметров авторизации:

* `yourAppActivity`: activity вашего приложения, из которого будет осуществляться вызов авторизации
* `AUTH_REQUEST_CODE`: код обработки результата на onActivityResult
* `REDIRECT_URI`: URI страницы приложения, на который OAuth-сервер осуществляет передачу события результата 
авторизации. Значение этого параметра при посимвольном сравнении должно быть идентично значению redirectUri, 
указанному при регистрации приложения. При сравнении не учитываются индивидуальные параметры приложения, 
которые могут быть добавлены в конец строки URI
* `getPermissions()`: коллекция прав, возможные права можно посмотреть в описании библиотеки 
[yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamolib)
* `showResultDialog`: флаг, говорящий нужно ли показывать в случае успешного результата запроса диалог 
успеха для пользователя
* `dialogListener`: экземпляр класса, имплементирующего интерфейс `YandexMoneyDroid.DialogListener`

```java
    public static interface DialogListener {
    
        public void onSuccess(Bundle values);
        
        public void onFail(String cause);
        
        public void onException(Exception exception);
        
        public void onCancel();
    }
```
    
В нем вы реализуете обработку возможных результатов запросов. Отлично запрос сделали. Но теперь нам 
нужно разрешить библиотеке обрабатывать результат запроса. Для этого на `onActivityResult` activity вашего
приложения добавляем следующий код:

```java
ymd.callbackOnResult(requestCode, resultCode, data);
```

Ну, и последнее, на `onSuccess` dialogListener’а получаем токен пользователя:

```java
String token = values.getString(ActivityParams.AUTH_OUT_ACCESS_TOKEN);
```

Список выходных параметров других методов можно посмотреть в классе `ActivityParams`.


### Запрос истории операций и их деталей

```java
ymd.showHistory(yourAppActivity, CODE_HISTORY, accessToken, dialogListener);
```


### Вызов активити библиотеки вручную

Если вы по каким-то причинам не захотите пользоваться классом YandexMoneyDroid, а будет напрямую вызывать 
активити библиотеки, то для упрощения вызова Intent'ов библиотеки создан публичный класс IntentCreator, который четко знает какие параметры
нужно передать в том или ином случае. Посмотрите на javadoc статических методов этого класса.

#### Авторизация 
Чтобы авторизоваться и получить токен доступа к API, нужно сделать intent и вызвать его, ожидая результат. 
Код вызова Intent'а:

    Intent intent = IntentCreator.createAuth(YourAppActivity, "YOUR_APP_CLIENT_ID",
            YOUR_APP_REDIRECT_URI, getPermissions(), true);
    startActivityForResult(intent, CODE_AUTH);
    
Чтобы авторизоваться, нужно указать права которые будут доступны пользователю. Как это сделать - более подробно показано в описании [yamolib](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/yamolib) в секции [примеры использования](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/blob/master/yamolib/readme.md).  
Код обработки результата

    if (requestCode == CODE_AUTH) {
            boolean isSuccess = data.getBooleanExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);
            String accessToken = data.getStringExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN);
            String error = data.getStringExtra(ActivityParams.AUTH_OUT_ERROR);

            if (isSuccess)
                // сохраняем accessToken
                
            Toast.makeText(this, "Authorization result: " + isSuccess + "\ntoken: " + token + "\n" +
                            "error: " + error, Toast.LENGTH_LONG).show();
        }
        
В остальных случаях результат обрабатывается таким же образом, поэтому больше его упоминать не будем. 
Выходные параметры для обработки результата каждой функции можно посмотреть в классе `ActivityParams`.
        
#### Пример получения истории операций 
Покажем как показать историю операции пользователя:

    Intent historyIntent = IntentCreator.createHistory(YourAppActivity, "YOUR_APP_CLIENT_ID", accessToken);
    startActivity(historyIntent);
    
#### Пример перевода на счет другому пользователю

    Intent intent = IntentCreator.createPaymentP2P(YourAppActivity, "YOUR_APP_CLIENT_ID", accessToken,
                                "410011161616877", BigDecimal.valueOf(0.02), "comment for p2p", "message for p2p", true);
    startActivityForResult(intent, CODE_PAYMENT_P2P);
    
#### Пример вызова оплаты мобильной связи оператора Мегафон

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("PROPERTY1", "921");
    params.put("PROPERTY2", "3020052");
    params.put("sum", "1.00");

    Intent intent = IntentCreator.createPaymentShop(YourAppActivity, "YOUR_APP_CLIENT_ID", accessToken,
            BigDecimal.valueOf(1.00), "337", params, true);
    startActivityForResult(intent, CODE_PAYMENT_SHOP);    
