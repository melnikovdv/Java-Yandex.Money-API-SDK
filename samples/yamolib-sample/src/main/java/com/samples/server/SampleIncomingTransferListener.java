package com.samples.server;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.yandex.money.api.notifications.IncomingTransfer;
import ru.yandex.money.api.notifications.IncomingTransferListener;

import java.util.List;

/**
* <p/>
* <p/>
* Created: 07.12.13 21:04
* <p/>
*
* @author OneHalf
*/
public class SampleIncomingTransferListener implements IncomingTransferListener {

    private static final Log LOG = LogFactory.getLog(ServletListener.class);

    private static final List<IncomingTransfer> transferMap = Lists.newCopyOnWriteArrayList();
    private static final List<IncomingTransfer> testTransferMap = Lists.newCopyOnWriteArrayList();

    public static List<IncomingTransfer> getTransferList() {
        return transferMap;
    }

    public static List<IncomingTransfer> getTestTransferList() {
        return testTransferMap;
    }

    @Override
    public void processNotification(IncomingTransfer incomingTransfer) {
        LOG.info("receive " + incomingTransfer);
        transferMap.add(incomingTransfer);
    }

    @Override
    public void processTestNotification(IncomingTransfer testIncomingTransfer) {
        LOG.info("receive test " + testIncomingTransfer);
        testTransferMap.add(testIncomingTransfer);
    }
}
