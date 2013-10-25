package ru.yandex.money.api.rights;

/**
 * <p/>
 * <p/>
 * Created: 25.10.13 22:34
 * <p/>
 *
 * @author OneHalf
 */
public enum IdentifierType {

    EMAIL, ACCOUNT, PHONE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
