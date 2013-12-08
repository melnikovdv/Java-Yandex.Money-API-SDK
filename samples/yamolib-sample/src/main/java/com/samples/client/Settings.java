package com.samples.client;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author dvmelnikov
 */

public class Settings {

    public static final String CLIENT_ID;
    public static final String REDIRECT_URI;
    public static final String NOTIFICATION_SECRET;
    public static final String NOTIFICATION_URI;

    private static final Log LOG = LogFactory.getLog(Settings.class);


    static {
        Properties properties = new Properties();
        try {
            properties.load(Settings.class.getResourceAsStream("/settings.properties"));

            CLIENT_ID = properties.getProperty("client_id");
            REDIRECT_URI = properties.getProperty("redirect_uri");
            NOTIFICATION_SECRET = properties.getProperty("notification_secret");
            NOTIFICATION_URI = properties.getProperty("notification_uri");

            LOG.info("settings: " + Maps.fromProperties(properties));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
