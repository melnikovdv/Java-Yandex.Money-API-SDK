package ru.yandex.money.api.rights;

/**
 * @author dvmelnikov
 */

public class ShoppingCart extends AbstractPermission {

    public ShoppingCart() {
        super("shopping-cart");
    }

    @Override
    public String value() {
            return name + "(0.04,,\"643\",\"№123/2011\").to-pattern(\"337\").item(\"Japanese Green Tea Kabuse Aracha 7oz.\",,0.02).item(\"Japanese Green Tea Kabuse Aracha 7oz.2\",,0.02)";
    }
}
