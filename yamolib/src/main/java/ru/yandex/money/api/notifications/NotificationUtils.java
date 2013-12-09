package ru.yandex.money.api.notifications;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationUtils implements Serializable {

    private static final Log LOG = LogFactory.getLog(NotificationUtils.class);

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

    public boolean isHashValid(Map<String, String> parameterMap, String secret) {
        Map<String, String> map = new HashMap<String, String>(parameterMap);
        map.put("notification_secret", secret);

        checkAllParametersNotNull(map);

        String realHash = calculateHash(map);
        String sha1HashParam = map.get("sha1_hash");

        boolean equals = realHash.equalsIgnoreCase(sha1HashParam);
        if (!equals) {
            LOG.debug("the hashes are not equals. expected: " + realHash + ", but received: " + sha1HashParam);
        }
        return equals;
    }

    private void checkAllParametersNotNull(Map<String, String> map) {
        for (String s : EXPECTED_PARAMS_ARRAY) {
            if (!map.containsKey(s)) {
                throw new IllegalArgumentException("param " + s + " is absent");
            }
        }
    }

    public String calculateHash(Map<String, String> parameterMap, String secret) {
        Map<String, String> map = new HashMap<String, String>(parameterMap);
        map.put("notification_secret", secret);

        checkAllParametersNotNull(map);
        return calculateHash(map);
    }

    private String calculateHash(Map<String, String> parameterMap) {
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