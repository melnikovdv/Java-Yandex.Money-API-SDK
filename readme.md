##Java-проекты для API Яндекс.Денег

- - -

**aggregator** (aka Java Yandex Money Api) - проект-агрегатор для.

**yamolib** (aka Yandex Money Library) - java-библиотека для работы с API. Ссылка на readme:

**yamodroid** (aka Yandex Money Droid Library) - android-библиотека для работы c API. Ссылка на readme:

**samples** - проект-агрегатор для тестовых приложений использования библиотек.

**yamolib-sample** - тестовое приложение для yamolib

**yamodroid-sample** - тестовое приложение для yamodroid

###Структура проекта

    aggregator
    |
    |- yamolib
    |
    |- yamodroid
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