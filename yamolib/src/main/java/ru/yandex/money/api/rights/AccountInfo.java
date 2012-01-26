package ru.yandex.money.api.rights;

/**
 * Право на получение информации о состоянии счета
 * @author dvmelnikov
 */

public class AccountInfo extends AbstractPermission {
    public AccountInfo() {
        super("account-info");
    }
}
