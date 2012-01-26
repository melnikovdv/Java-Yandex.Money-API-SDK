package ru.yandex.money.api.rights;

/**
 * Абстрактный класс права на доступ к счету пользователя с ограничением
 * лимита платежа и назначением (в конкретный магазин и на конкретный счет
 * пользователя)
 * @author dvmelnikov
 */

public class AbstractDestinationPermission extends AbstractLimitedPermission {

    protected String destination;

    protected AbstractDestinationPermission(String name) {
        super(name);
    }

    protected Permission destinationToPattern(String patternId) {
        destination = "to-pattern(\"" + patternId + "\")";
        return this;
    }

    protected Permission destinationToAccount(String account) {
        destination = "to-account(\"" + account + "\")";
        return this;
    }

    public String value() {
        String res = name + "." + destination;
        if (limit != null)
            res = res + "." + limit;
        return res;
    }

}
