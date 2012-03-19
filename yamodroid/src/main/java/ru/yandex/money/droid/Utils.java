package ru.yandex.money.droid;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;

/**
 * @author dvmelnikov
 */

public class Utils {
    
//    private static Map<String, YandexMoney> ymObjects = new HashMap<String, YandexMoney>();

    public static YandexMoney getYandexMoney(String clientId) {
        //todo: разобраться с этим!
//        if ((clientId == null) || (clientId.equals("")))
//            throw new IllegalArgumentException("client_id is empty");
//
//        if (ymObjects.containsKey(clientId))
//            return ymObjects.get(clientId);
//        else {                                                
            YandexMoney ym = new YandexMoneyImpl(clientId,
                    AndroidHttpClient.newInstance(Consts.USER_AGENT));
//            ymObjects.put(clientId, ym);
            return ym;
//        }
    }

    public static ProgressDialog makeProgressDialog(Context context, String message) {
        return makeProgressDialog(context, null, message);
    }

    public static ProgressDialog makeProgressDialog(Context context, String title, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        if (title != null) {
            dialog.setTitle(title);
            dialog.setIcon(R.drawable.ic_wallet);
        }
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
