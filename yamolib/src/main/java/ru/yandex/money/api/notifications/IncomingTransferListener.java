package ru.yandex.money.api.notifications;

/**
* <p/>
* <p/>
* Created: 06.12.13 9:06
* <p/>
*
* @author OneHalf
*/
interface IncomingTransferListener {

    void process(IncomingTransfer incomingTransfer);
}
