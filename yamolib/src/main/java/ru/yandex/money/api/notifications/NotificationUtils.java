package ru.yandex.money.api.notifications;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationUtils implements Serializable {

    private static final Log LOG = LogFactory.getLog(Notifications.class);

    static final String STRING_FOR_HASH
            = "notification_type&operation_id&amount&currency&datetime&sender&codepro&notification_secret&label";

    public NotificationUtils() {
    }

    boolean isHashValid(Map<String, String> parameterMap, String secret) {
        String realHash = calculateHash(parameterMap, secret);
        String sha1HashParam = parameterMap.get("sha1_hash");

        return realHash.equalsIgnoreCase(sha1HashParam);
    }

    String calculateHash(Map<String, String> parameterMap, String secret) {
        String stringForHash = createStringForHash(parameterMap, secret);
        return Hex.encodeHexString(DigestUtils.sha1(stringForHash));
    }

    String createStringForHash(Map<String, String> parameterMap, String secret) {
        List<String> strings = new ArrayList<String>();
        for (String paramName : STRING_FOR_HASH.split("&")) {
            if ("notification_secret".equals(paramName)) {
                strings.add(secret);
                continue;
            }
            strings.add(parameterMap.get(paramName));
        }

        StringBuilder stringForHash = new StringBuilder(strings.get(0));
        for (String param : strings.subList(1, strings.size())) {
            stringForHash.append('&').append(param);
        }
        return stringForHash.toString();
    }
}