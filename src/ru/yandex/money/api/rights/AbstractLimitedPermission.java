package ru.yandex.money.api.rights;

import java.util.regex.Pattern;

/**
 * Абстрактный класс права на доступ к счету пользователя с ограничением
 * лимита платежа
 * @author dvmelnikov
 */

public class AbstractLimitedPermission extends AbstractPermission {

    protected String limit;

    protected AbstractLimitedPermission(String name) {
        super(name);
    }

    /**
     * Ограничение права на лимит платежа. Ограничивает общую сумму платежей за
     * период времени в сутках.
     * @param duration период времени в сутках
     * @param sum лимит суммы
     * @return само себя (право)
     */
    protected Permission limit(int duration, String sum) {
        limit = "limit(" + duration + "," + sum + ")";

        if (!checkSum(sum))
            throw new IllegalArgumentException("sum is not valid");

        return this;
    }

    private boolean checkSum(String sum) {
        Pattern p = Pattern.compile("\\d{1,15}(|\\.\\d\\d)");
        return p.matcher(sum).matches();
    }

    public String value() {
        String res = super.value();
        if (limit != null)
            res = res + "." + limit;
        return res;
    }
}
