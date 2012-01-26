package ru.yandex.money.droid.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.droid.R;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.preferences.Prefs;

/**
 * @author dvmelnikov
 */

public class Utils {

    private static YandexMoney ym;
    private static AndroidHttpClient httpClient = AndroidHttpClient
            .newInstance("AndroidHttpClient");

    public static YandexMoney getYandexMoney(Context context) {
        if (ym == null) {
            String clientId = new Prefs(context).read(LibConsts.PREF_CLIENT_ID);
            if (clientId == null)
                throw new IllegalStateException("client_id not found");
            return new YandexMoneyImpl(clientId, httpClient);
        } else
            return ym;
    }

    public static void writeToken(Context context, String clientId, String token) {
        Prefs prefs = new Prefs(context);
        prefs.write(clientId, token);
    }

    public static String getToken(Context context, String clientId) {
        Prefs prefs = new Prefs(context);
        String token = prefs.read(clientId);
        return token.equals("") ? null : token;
    }
    
    public static String getRedirectUri(Context context) {
        return new Prefs(context).read(LibConsts.PREF_REDIRECT_URI, "");
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
        dialog.setCancelable(false);
        return dialog;
    }
    
    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static AlertDialog makeResultAlertDialog(final Activity context, String title, String successMessage, final String error) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
            builder.setIcon(R.drawable.ic_wallet);
        }
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (error == null)
                    context.setResult(Activity.RESULT_OK);
                else
                    context.setResult(Activity.RESULT_CANCELED);
                context.finish();
            }
        });

        if (error == null)
            builder.setMessage(successMessage);
        else
            builder.setMessage("Ошибка: " + error);

        return builder.create();
    }
}
