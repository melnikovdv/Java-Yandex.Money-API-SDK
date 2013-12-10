package ru.yandex.money.api.notifications;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p/>
 * <p/>
 * Created: 05.12.13 9:16
 * <p/>
 *
 * @author OneHalf
 */
public class NotificationUtilsTest {

    public static final NotificationUtils NOTIFICATION_UTILS = new NotificationUtils();
    public static final String SECRET = "0UyvT/YmMb9ed8FA6rsrYXqP";

    String string = "p2p-incoming&818163584552108017&2.23&643&2012-12-17T17:49:52Z&410011608243693&false&0UyvT/YmMb9ed8FA6rsrYXqP&12625";

    @Test
    public void testCheckHash() throws Exception {
        assertTrue(NOTIFICATION_UTILS.isHashValid(createParamsMap(), SECRET));
    }

    @Test
    public void testCheckHash2() throws Exception {
        Map<String, String> map = createParamsMap();

        assertEquals("b9d4dee98caec486a8a3b1a577fce7efd0e7f0fb", NOTIFICATION_UTILS.calculateHash(map, SECRET));
    }

    @Test
    public void testString() {
        Map<String, String> paramsMap = createParamsMap();
        paramsMap.put("notification_secret", SECRET);
        assertEquals(string, NOTIFICATION_UTILS.createStringForHash(paramsMap));
    }

    private Map<String, String> createParamsMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("notification_type", "p2p-incoming");
        map.put("operation_id", "818163584552108017");
        map.put("amount", "2.23");
        map.put("currency", "643");
        map.put("datetime", "2012-12-17T17:49:52Z");
        map.put("sender", "410011608243693");
        map.put("codepro", "false");
        map.put("label", "12625");
        map.put("sha1_hash", "b9d4dee98caec486a8a3b1a577fce7efd0e7f0fb");

        return map;
    }
}
