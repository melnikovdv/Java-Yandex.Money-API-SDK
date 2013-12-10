package ru.yandex.money.api;

import java.math.BigInteger;

/**
 * Объекты класса <code>YamoneyAccount</code> представляют номер счета в Яндекс.Деньгах.
 */
public final class YamoneyAccount implements Comparable<YamoneyAccount> {

    private static final int N_LEN = 1;
    private static final int Z_LEN = 2;
    private static final int Y_MAX_LEN = 19;
    private static final BigInteger INT_99 = BigInteger.valueOf(99L);
    private static final int p[][] = new int[10][30];

    static {
        BigInteger m_13 = BigInteger.valueOf(13L);

        BigInteger[] m = new BigInteger[30];
        m[0] = BigInteger.valueOf(169L);
        for (int i = 1; i < m.length; i++) {
            m[i] = m[i - 1].multiply(m_13);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger t_i = BigInteger.valueOf(i != 0 ? i : 10);
            for (int j = 0; j < 30; j++) {
                p[i][j] = t_i.multiply(m[j]).mod(INT_99).intValue();
            }
        }
    }

    private static boolean isDigit(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Номер счета в строковом представлени
     */
    private String account;

    /**
     * @param account номер счета в системе Яндекс.Деньги
     * @throws IllegalArgumentException Если переданная последовательность не соответствует установленному формату.
     */
    public YamoneyAccount(String account) {
        if (!isValidAccountNumber(account)) {
            throw new IllegalArgumentException("wrong account number: " + account);
        }
        this.account = account;
    }

    /**
     * Номер счета в системе Яндекс.Деньги
     *
     * @return -
     */
    public String getAccount() {
        return account;
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof YamoneyAccount))
            return false;
        YamoneyAccount a = (YamoneyAccount) o;
        return account.equals(a.account);
    }

    @Override
    public String toString() {
        return account;
    }

    @Override
    public int compareTo(YamoneyAccount o) {
        return account.compareTo(o.getAccount());
    }

    public static String calculateRedundancy(String x, String y) {
        int[] t = new int[30]; // 0-9

        for (int t_i = 0, y_j = y.length() - 1; y_j >= 0; y_j--, t_i++)
            t[t_i] = Character.digit(y.charAt(y_j), 10);

        for (int t_i = 20, x_i = x.length() - 1; x_i >= 0; x_i--, t_i++)
            t[t_i] = Character.digit(x.charAt(x_i), 10);

        int result = 0;
        for (int i = 0; i < t.length; i++)
            result += p[t[i]][i];

        String _z = String.valueOf(result % INT_99.intValue() + 1);
        if (_z.length() == 1)
            _z = '0' + _z;
        return _z;
    }

    /**
     * Осуществляет вычленение данных из входящей последовательности.
     *
     * @throws IllegalArgumentException в случае невозможности разобрать входящую последовательность.
     */
    public static boolean isValidAccountNumber(String source) {
        if (source == null || source.isEmpty() || !isDigit(source)) {
            return false;
        }

        int n = fetchN(source);
        String x = fetchX(source, n);
        if (x == null) {
            return false;
        }
        String y = fetchY(source, n);
        if (y == null) {
            return false;
        }
        String z = fetchZ(source);
        return z != null && calculateRedundancy(x, y).equals(z);

    }

    private static int fetchN(String source) {
        int _n = Integer.parseInt(source.substring(0, N_LEN));
        return _n == 0 ? 10 : _n;
    }

    private static String fetchX(String source, int n) {
        if (source.length() < N_LEN + n) {
            return null;
        }
        String _x = source.substring(N_LEN, N_LEN + n);
        if (_x.charAt(0) == '0') {
            return null;
        }
        return  _x;
    }

    private static String fetchY(String source, int n) {
        int start_index = N_LEN + n;
        int end_index = source.length() - Z_LEN;
        if (end_index <= start_index) {
            return null;
        }
        if (end_index - start_index > Y_MAX_LEN) {
            return null;
        }
        String _y = source.substring(start_index, end_index);
        if (_y.startsWith("0")) {
            return null;
        }
        return _y;
    }

    private static String fetchZ(String source) {
        String _z = source.substring(source.length() - Z_LEN, source.length());
        if (_z.startsWith("00")) {
            return null;
        }
        return  _z;
    }
}
