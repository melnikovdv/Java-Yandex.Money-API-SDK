package com.samples.yamodroid;

import ru.yandex.money.api.enums.Destination;
import ru.yandex.money.api.rights.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author dvmelnikov
 */

public class Consts {
    public static String CLIENT_ID = "24C38EA565ECEDF1E7501628163E4E5C08B3FB8F6FA7B2E957AE0A0DA8159464";
    public static String REDIRECT_URI = "yamodroidtest://localhost/authresult";//"http://localhost/authredirect";

    public static Collection<Permission> getPermissions() {
        Collection<Permission> scope = new LinkedList<Permission>();
        scope.add(new AccountInfo());
//        scope.add(new OperationHistory());
//        scope.add(new OperationDetails());
        scope.add(new MoneySource(true, true));
        scope.add(new PaymentP2P().limit(30, "100"));
        scope.add(new Payment(Destination.toPattern, "337", 1, "10"));        
        return scope;
    }
}
