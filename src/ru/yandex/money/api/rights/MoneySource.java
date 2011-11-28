package ru.yandex.money.api.rights;

/**
 * Указание доступных методов проведения платежа: с кошелькая счета или с
 * привязанной банковской карты
 * @author dvmelnikov
 */

public class MoneySource extends AbstractPermission {

    private boolean wallet;
    private boolean card;

    private String WALLET = "wallet";
    private String CARD = "card";

    /**
     * Создание права money-source
     * @param wallet разрешение на оплату с кошелька счета
     * @param card разрешение на оплату с привязанной банковской карты
     */
    public MoneySource(boolean wallet, boolean card) {
        super("money-source");
        this.wallet = wallet;
        this.card = card;
    }

    public String value() {
        String params;
        if (wallet && card)
             params = source(WALLET) + "," + source(CARD);
        else  {
            if (card)
                params = source(CARD);
            else
                params = source(WALLET);
        }
        return super.value() + "(" + params + ")";
    }

    private String source(String name) {
        return "\"" + name + "\"";
    }
}
