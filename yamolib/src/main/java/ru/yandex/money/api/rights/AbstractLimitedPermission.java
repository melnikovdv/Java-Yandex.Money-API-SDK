package ru.yandex.money.api.rights;

import java.util.regex.Pattern;

/**
 * Абстрактный класс права на доступ к счету пользователя с ограничением
 * лимита платежа
 * @author dvmelnikov
 */

public abstract class AbstractLimitedPermission extends AbstractPermission {

    public static final Pattern SUM_PATTERN = Pattern.compile("\\d{1,15}(\\.\\d\\d)?");

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
    public Permission limit(int duration, String sum) {
        checkSum(sum);

        limit = "limit(" + duration + "," + sum + ")";
        return this;
    }

    /**
     * Ограничение права на лимит платежа и количетсво платежей. Создает разрешение на 
     * одноразовый платеж определенной суммы      
     * @param sum лимит суммы
     * @return само себя (право)
     */
    public Permission limit(String sum) {
        checkSum(sum);

        limit = "limit(," + sum + ")";
        return this;
    }

    private void checkSum(String sum) {
        if (!SUM_PATTERN.matcher(sum).matches()) {
            throw new IllegalArgumentException("sum is not valid");
        }
    }

    @Override
    public String value() {
        String rule = super.value();
        if (limit != null) {
            return rule + "." + limit;
        }
        return rule;
    }
}
