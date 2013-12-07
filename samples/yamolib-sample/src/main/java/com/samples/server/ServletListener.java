package com.samples.server;

import com.samples.client.Settings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.yandex.money.api.notifications.IncomingTransfer;
import ru.yandex.money.api.notifications.IncomingTransferListener;
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

    private static final Log LOG = LogFactory.getLog(ServletListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        NotificationsServlet.setSecret(Settings.NOTIFICATION_SECRET);
        NotificationsServlet.setListener(new IncomingTransferListener() {
            @Override
            public void processNotification(IncomingTransfer incomingTransfer) {
                LOG.info("receive " + incomingTransfer);
            }

            @Override
            public void processTestNotification(IncomingTransfer incomingTransfer) {
                LOG.info("receive test " + incomingTransfer);
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
