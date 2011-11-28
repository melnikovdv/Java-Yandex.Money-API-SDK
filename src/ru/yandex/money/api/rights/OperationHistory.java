package ru.yandex.money.api.rights;

/**
 * Право на просмотр истории операций
 * @author dvmelnikov
 */

public class OperationHistory extends AbstractPermission {

    public OperationHistory() {
        super("operation-history");
    }
}
