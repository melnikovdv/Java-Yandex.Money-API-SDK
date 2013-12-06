package ru.yandex.money.api.notifications;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * <p/>
 * Created: 04.12.13 23:32
 * <p/>
 *
 * @author OneHalf
 */
public class NotificationsServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(NotificationsServlet.class);
    public static final String SECRET = "12345";

    private final NotificationUtils notificationUtils = new NotificationUtils();

    private volatile static IncomingTransferListener listener;

    public static void setListener(IncomingTransferListener listener) {
        NotificationsServlet.listener = listener;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (listener == null) {
            throw new IllegalStateException("servlet state is not initialised");
        }

        Map<String, String> parametersMap = createParametersMap(request);
        if (!notificationUtils.isHashValid(parametersMap, SECRET)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "SHA-1 hash verification failed") ;
            LOG.warn("SHA-1 hash verification failed: " + compileLogRecord(request, null)) ;
            return;
        }

        try {
            // получить значения параметров уведомления
            final String notificationType = parametersMap.get("notification_type") ;
            final String operationId = parametersMap.get("operation_id") ;
            final String sender = parametersMap.get("sender") ;
            final String label = parametersMap.get("label") ;
            final boolean testNotification = Boolean.parseBoolean(request.getParameter("test_notification"));
            final BigDecimal amount = new BigDecimal(parametersMap.get("amount")) ;
            final int currency = Integer.parseInt(parametersMap.get("currency")) ;
            final Date datetime = XMLGregorianCalendarImpl.parse(parametersMap.get("datetime")).toGregorianCalendar().getTime() ;
            final boolean codepro = Boolean.parseBoolean(parametersMap.get("codepro")) ;

            // проверка факта того что уведомление тестовое
            if (testNotification) {
                LOG.info("Test notification has received.") ;
                return; // response is HTTP 200 OK without content
            }

            if (!"p2p-incoming".equals(notificationType)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported notification type: " + notificationType) ;
                return;
            }

            // response is HTTP 200 OK without content
            listener.process(new IncomingTransfer(operationId, amount, currency, datetime, sender, codepro, label)) ;

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage()) ;
            LOG.warn(compileLogRecord(request, e)) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private Map<String, String> createParametersMap(HttpServletRequest req) {
        Map<String, String> parametersMap = new HashMap<String, String>();

        @SuppressWarnings("unchecked")
        Enumeration<String> parameterNames = req.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            parametersMap.put(paramName, req.getParameter(paramName));
        }
        return parametersMap;
    }


    private static String compileLogRecord(HttpServletRequest request, Throwable e) {
        StringBuilder sb = new StringBuilder() ;
        if (e != null) sb.append(e.getMessage()).append(" : ") ;
        sb.append("HttpServletRequest={ IP:").append(request.getRemoteAddr())
                .append(':').append(request.getRemotePort())
                .append(" URL:").append(request.getRequestURL()) ;

        @SuppressWarnings("unchecked")
        Map<String, String[]> params = request.getParameterMap() ;

        sb.append("} Parameters={\n") ;
        for (String p : params.keySet()) {
            String[] v = params.get(p) ;
            sb.append(p).append('=').append(v != null ? v[0] : "NULL").append('\n') ;
        }
        sb.append("} }") ;
        return sb.toString();
    }
}
