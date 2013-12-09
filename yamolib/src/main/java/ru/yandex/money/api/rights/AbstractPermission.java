package ru.yandex.money.api.rights;

/**
 * Абстрактный класс права на доступ к счету пользователя
 * @author dvmelnikov
 */

public abstract class AbstractPermission implements Permission {

    protected String name;

    protected AbstractPermission(String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return name;
    }
}
