package ru.yandex.money.api.rights;

/**
 * Право на возможность осуществлять платежи во все доступные для API магазины
 * @author dvmelnikov
 */

public class PaymentShop extends AbstractLimitedPermission {

    public PaymentShop() {
        super("payment-shop");
    }
}
