package com.samples.client;

import java.io.IOException;
import java.util.Properties;

/**
 * @author dvmelnikov
 */

public class Consts {

    public static final String CLIENT_ID;
    public static final String REDIRECT_URI;
    public static final String NOTIFICATION_SECRET;

    static {
        Properties properties = new Properties();
        try {
            properties.load(Consts.class.getResourceAsStream("/settings.properties"));

            CLIENT_ID = properties.getProperty("client_id");
            REDIRECT_URI = properties.getProperty("redirect_uri");
            NOTIFICATION_SECRET = properties.getProperty("notification_secret");

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
