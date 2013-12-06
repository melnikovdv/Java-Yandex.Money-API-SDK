package ru.yandex.money.api.notifications;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationUtils implements Serializable {

    public static final String[] EXPECTED_PARAMS_ARRAY = new String[]{
            "notification_type",
            "operation_id",
            "amount",
            "currency",
            "datetime",
            "sender",
            "codepro",
            "notification_secret",
            "label"};

    private static final String DELIMITER = "&";


    boolean isHashValid(Map<String, String> parameterMap, String secret) {
        Map<String, String> map = new HashMap<String, String>(parameterMap);
        map.put("notification_secret", secret);

        checkAllParametersNotNull(map);

        String realHash = calculateHash(parameterMap);
        String sha1HashParam = parameterMap.get("sha1_hash");

        return realHash.equalsIgnoreCase(sha1HashParam);
    }

    private void checkAllParametersNotNull(Map<String, String> map) {
        for (String s : EXPECTED_PARAMS_ARRAY) {
            if (!map.containsKey(s)) {
                throw new IllegalArgumentException("param " + s + " is absent");
            }
        }
    }

    String calculateHash(Map<String, String> parameterMap) {
        String stringForHash = createStringForHash(parameterMap);
        return Hex.encodeHexString(DigestUtils.sha1(stringForHash));
    }

    String createStringForHash(Map<String, String> parameterMap) {
        List<String> strings = new ArrayList<String>();
        for (String paramName : EXPECTED_PARAMS_ARRAY) {
            strings.add(parameterMap.get(paramName));
        }

        StringBuilder stringForHash = new StringBuilder(strings.get(0));
        for (String param : strings.subList(1, strings.size())) {
            stringForHash.append(DELIMITER).append(param);
        }
        return stringForHash.toString();
    }
}