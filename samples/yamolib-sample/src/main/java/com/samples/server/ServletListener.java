package com.samples.server;

import com.samples.client.Settings;
import ru.yandex.money.api.notifications.NotificationsServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p/>
 * <p/>
 * Created: 07.12.13 17:55
 * <p/>
 *
 * @author OneHalf
 */
public class ServletListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        NotificationsServlet.setSecret(Settings.NOTIFICATION_SECRET);
        NotificationsServlet.setListener(new SampleIncomingTransferListener());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
