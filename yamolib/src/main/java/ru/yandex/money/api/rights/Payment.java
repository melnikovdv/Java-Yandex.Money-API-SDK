package ru.yandex.money.api.rights;

import ru.yandex.money.api.enums.Destination;

/**
 * Право на возможность осуществлять платежи в конкретный магазин или переводить средства
 * на конкретный счет пользователя
 * @author dvmelnikov
 */

public class Payment extends AbstractDestinationPermission {

    public Payment(Destination destinationType, String destination, int duration, String sum) {
        super("payment");
        if (destinationType == Destination.toAccount)
            destinationToAccount(destination);
        if (destinationType == Destination.toPattern)
            destinationToPattern(destination);
        this.limit(duration, sum);
    }
}
