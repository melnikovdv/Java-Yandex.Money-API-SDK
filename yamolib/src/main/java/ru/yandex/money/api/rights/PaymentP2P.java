package ru.yandex.money.api.rights;

/**
 * Право на возможность переводить средства на любые счета других пользователей
 * @author dvmelnikov
 */

public class PaymentP2P extends AbstractLimitedPermission {

    public PaymentP2P() {
        super("payment-p2p");
    }
}
