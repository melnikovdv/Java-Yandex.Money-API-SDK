package ru.yandex.money.droid.utils2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import ru.yandex.money.droid.R;

/**
 * @author dvmelnikov
 */

public class Utils2 {

    public static AlertDialog makeResultAlertDialog(final Activity activity,
            final String title, final String successMessage, final String error) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);
        if (title != null) {
            builder.setTitle(title);
            builder.setIcon(R.drawable.ic_wallet);
        }
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (error == null) && ()
                    activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });

        if (error == null)
            builder.setMessage(successMessage);
        else
            builder.setMessage("Ошибка: " + error);

        return builder.create();
    }
}
