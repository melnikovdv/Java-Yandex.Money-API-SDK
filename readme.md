##Java-проекты для API Яндекс.Денег

- - -

**aggregator** (aka Java Yandex Money Api) - проект-агрегатор.

**yamolib** (aka Yandex Money Library) - java-библиотека для работы с API. [Посмотреть](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamolib).

**yamodroid** (aka Yandex Money Droid Library) - **deprecated** android-библиотека для работы c API. [Посмотреть](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/yamodroid).

**samples** - проект-агрегатор для тестовых приложений использования библиотек. [Посмотреть](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/samples).

**yamolib-sample** - тестовое приложение для yamolib. [Посмотреть](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/samples/yamolib-sample).

**yamodroid-sample** - тестовое приложение для yamodroid. [Посмотреть](https://github.com/melnikovdv/Java-Yandex.Money-API-SDK/tree/master/samples/yamodroid-sample).

###Структура проекта

    aggregator
    |
    |- yamolib
    |
    |- yamodroid (deprecated)
    |
    |- samples
        |
        |- yamolib-sample
        |
        |- yamodroid-sample

###Инструкции по сборке

Все проекты можно собрать с помощью [Maven](http://en.wikipedia.org/wiki/Apache_Maven) и pom-файлов. Инструкция по установке Maven [тут](http://maven.apache.org/download.html).   
GettingStarted Maven-плагина для Android [тут](http://code.google.com/p/maven-android-plugin/wiki/GettingStarted).

Чтобы создать файлы проекта для вашей среды разработки, нужно в каталоге какого-либо проекта выполнить команду `mvn idea:idea` или `mvn eclipse:eclipse` в зависимости от вашей IDE. NetBeans не упомянут в виду отсутствия у него плагина для android-разработки.

### Changelog

**02.02.14 yamolib 1.2.4**

* Поправлена логика тестовых платежей. (Убрано использование GET-параметров)
* Убрано исключение InsufficientScopeException из команд запроса токена

**08.12.13 yamolib 1.2.3**

* Добавлен класс YamoneyAccount с логикой проверки корректности номера счета
* Добавлены классы для обработки http-уведомлений о входящем платеже. (NotificationUtils, NotificationServlet)

**27.11.13 yamolib 1.2.2**

* Коды ошибок сделаны перечислениями (enum). Может сломать логику приложения, если вы используете выражения вида "response.getError().equals("limit_exceeded")"

**21.11.13 yamolib 1.2.1**

* Добавлено логирование вызовов api

**18.11.13 yamolib 1.2.0**

* На request-payment добавлены данные привязанной карты. (тип, часть номера, возможность платить без csc)

**30.10.2013**

* теперь можно отдельно использовать функционал запроса токена и команды с его применением.
* для упрощения тестирования добавлена возможность использования произвольного Url вместо "настоящего" url Яндекс.Деньги
* добавлено использование identifier_type в формирование scope и в request-payment для p2p. (Для указания получателя, теперь можно использовать его телефон или email)
* добавлены поля account_type и identified в ответ команды account-info
* добавлено поле label (метка платежа) в выдачу команд operation-history и operation-details
* добавлено поле status в выдачу команд operation-history и operation-details (для определения отклоненных и/или незавершенных платежей с протекцией)
* добавлена возможность запросить детали операций в operation-history, добавив параметр details=true
* добавлен запрос истории платежей по периоду времени (параметры from и till)
* добавлен запрос истории платежей по метке (параметр label)
* добавлена возможность указать метку платежа в request-payment для p2p
* добавлена возможность выполнить request-payment для p2p с указанием суммы к получению (парметр amount_due)
* поддержка тестовых платежей (параметры test_payment, test_card, test_result)
* в request-payment для p2p добавлены поля с информацией о получателе (recipient_identified и recipient_account_type)
* в команды request-payment и process-payment добавлены поля error_description, в которыъ могут приходить описания ошибок
* добавлен метод вызова request-payment c patternId='phone-topup' с автоматическим определением сотового оператора по номеру телефона
* добавлена возможность формирования url для запроса токена по уже готовой строке scope
* мелкие баги поправлены

**12.07.2013**

* fix http connection handling

**25.03.2013 yamolib**

* token revokation support

**12.04.2012 yamodroid 1.1.1 and yamolib 1.1.1**

* добавлена поддержка client_secret в yamodroid и прокинут метод в интерфейс yamolib

**20.03.2012 yamodroid 1.1.0**

* добавлен класс YandexMoneyDroid и интерфейс лисенера для удобной обработки результатов работы методов API (onSuccess,
  onFail, onException, onCancel)
* добавлена обработка нажатия cancel (назад) на длительных операциях
* изменен и оптимизирован интерфейс работы оплаты в магазины, добавлено новое activity PaymentConfirmActivity.

**08.02.2012 yamolib 1.1.0** (спасибо [axet](https://github.com/axet))

* response-объекты стали serializable
* мелкие баги поправлены
* client_secret добавлен

    
**07.02.2012 yamodroid 1.0.0**

* создана библиотека для android
    
**01.02.2012 aggregator**

* поддержка maven
