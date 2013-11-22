package ru.yandex.money.api.response.util.money;

import java.io.Serializable;

/**
 * Доступные для приложения методы проведения платежа,
 * Присутствует только при успешном выполнении методов {@link ru.yandex.money.api.YandexMoney} requestPayment*.
 *
 * @author dvmelnikov
 */

public class PaymentMethods implements Serializable {

    private Element wallet;
    private CardElement card;

    private PaymentMethods() {
    }

    /**
     * Если возращает <code>null</code>, значит на счету нет достаточного количества средств для платежа
     * @return объект типа {@link ru.yandex.money.api.response.util.money.PaymentMethods.Element}
     */
    public Element getWallet() {
        return wallet;
    }

    /**
     * Если возращает <code>null</code>, значит к счету не привязана карта
     * @return объект типа {@link ru.yandex.money.api.response.util.money.PaymentMethods.Element}
     */
    public CardElement getCard() {
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
    public class Element  implements Serializable {

        protected boolean allowed = false;

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

    public class CardElement extends Element {
        /**
         * Признак разрешенности платежа с карты без card security code
         * Возможен для некоторых типов платежей, для которых низка вероятность мошенничества.
         */
        private boolean csc_required = true;
        /**
         * Маскированный номер карты
         */
        private String pan_fragment;

        /**
         * Тип карты (VISA, MasterCard)
         */
        private String type;

        public boolean isCscRequired() {
            return csc_required;
        }

        public String getPanFragment() {
            return pan_fragment;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "CardElement{" +
                    "allow=" + allowed +
                    ", csc_required=" + csc_required +
                    ", pan_fragment='" + getPanFragmentForLog() + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        private String getPanFragmentForLog() {
            return pan_fragment == null ? null : pan_fragment.replaceAll("^\\d+", "");
        }
    }
}
