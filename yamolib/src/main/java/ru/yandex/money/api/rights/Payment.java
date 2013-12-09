package ru.yandex.money.api.rights;

import ru.yandex.money.api.enums.Destination;

/**
 * Право на возможность осуществлять платежи в конкретный магазин или переводить средства
 * на конкретный счет пользователя
 * @author dvmelnikov
 */

public class Payment extends AbstractLimitedPermission {

    private String destination;

    public Payment() {
        super("payment");
    }

    public Payment(Destination destinationType, String destination, int duration, String sum) {
        this();
        setDestination(destinationType, destination).limit(duration, sum);
    }

    /**
     * Одноразовый токен
     * @param destinationType Тип платежа: в магазин, либо p2p
     * @param destination Получатель
     * @param sum Сумма платежа
     */
    public Payment(Destination destinationType, String destination, String sum) {
        this();
        setDestination(destinationType, destination).limit(sum);
    }

    private AbstractLimitedPermission setDestination(Destination destinationType, String destination) {
        if (destinationType == Destination.toAccount)
            return toAccount(destination);
        else if (destinationType == Destination.toPattern)
            return toPattern(destination);

        return this;
    }

    /**
     * Указание паттерна платежа в магазин, подключенный к Яндекс.Деньги
     * @param patternId Идентификатор шаблона платежа. Перейти на страницу оплаты для этого
     *                  магазина можно по ссылке https://money.yandex.ru/shop.xml?scid={patternId}
     * @return этот же обхект для дальнейшего указания лимитов
     */
    public AbstractLimitedPermission toPattern(String patternId) {
        destination = "to-pattern(\"" + patternId + "\")";
        return this;
    }

    public AbstractLimitedPermission toAccount(String account) {
        destination = "to-account(\"" + account + "\")";
        return this;
    }

    /**
     * Указание получателя p2p платежа и типа идентификатора. Можно выполнять платежи
     * идентифицируя получателя по его email, номеру телефона или номеру счета
     *
     * @param receiver Идентификатор получателя
     * @param identifierType Тип получателя
     * @return этот же обхект для дальнейшего указания лимитов
     */
    public AbstractLimitedPermission toAccount(String receiver, IdentifierType identifierType) {
        destination = "to-account(\"" + receiver + "\",\""+ identifierType + "\")";
        return this;
    }

    @Override
    public String value() {
        if (destination == null) {
            throw new IllegalStateException("destination is not specified");
        }

        String res = name + "." + destination;
        if (limit != null) {
            return res + "." + limit;
        }
        return res;
    }
}
