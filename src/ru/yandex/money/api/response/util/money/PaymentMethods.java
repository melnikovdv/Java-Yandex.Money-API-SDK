package ru.yandex.money.api.response.util.money;

/**
 * Доступные для приложения методы проведения платежа,
 * Присутствует только при успешном выполнении методов {@link ru.yandex.money.api.YandexMoney} requestPayment*.
 *
 * @author dvmelnikov
 */

public class PaymentMethods {

    private Element wallet;
    private Element card;

    private PaymentMethods() {
    }

    /**
     * @return объект типа {@link ru.yandex.money.api.response.util.money.PaymentMethods.Element}
     */
    public Element getWallet() {
        return wallet;
    }

    /**
     * @return объект типа {@link ru.yandex.money.api.response.util.money.PaymentMethods.Element}
     */
    public Element getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "PaymentMethods{" +
                "wallet=" + wallet +
                ", card=" + card +
                '}';
    }

    /**
     * Объект разрешения для проведения конкретного типа платежа.
     * Используется в объекте {@link PaymentMethods}
     *
     * @author dvmelnikov
     */
    public class Element {

        private boolean allowed = false;

        private Element() {
        }

        /**
         * Признак разрешен или запрещен платеж
         *
         * @return разрешен или нет
         */
        public Boolean getAllowed() {
            return allowed;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "allow=" + allowed +
                    '}';
        }
    }
}
