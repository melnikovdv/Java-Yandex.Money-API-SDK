package ru.yandex.money.api.rights;

/**
 * Указание доступных методов проведения платежа: с кошелькая счета или с
 * привязанной банковской карты
 * @author dvmelnikov
 */

public class MoneySource implements Permission {

    private static final String WALLET = "wallet";
    private static final String CARD = "card";


    private boolean wallet;
    private boolean card;

    /**
     * Создание права money-source
     * @param wallet разрешение на оплату с кошелька счета
     * @param card разрешение на оплату с привязанной банковской карты
     */
    public MoneySource(boolean wallet, boolean card) {
        this.wallet = wallet;
        this.card = card;
    }

    public String value() {
        if (!wallet && !card) {
            throw new IllegalArgumentException("money-source expected");
        }
        String params;
        if (wallet && card)
            params = WALLET + "\",\"" + CARD;
        else  {
            params = card ? CARD : WALLET;
        }
        return "money-source(\"" + params + "\")";
    }
}
