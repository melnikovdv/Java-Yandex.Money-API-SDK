package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ru.yandex.money.api.rights.Permission;

import java.util.Collection;

/**
 * User: mdv
 * Date: 09.03.12
 * Time: 14:31
 */
public class YandexMoneyDroid {

    private final String clientId;

    private int activityCodeAuth;
    private DialogListener dialogListenerAuth;

    private int activityCodeHistory;
    private DialogListener dialogListenerHistory;    

    public YandexMoneyDroid(String clientId) {
        this.clientId = clientId;
    }

    public void authorize(Activity activity, int activityCode, String redirectUri,
            Collection<Permission> permissions, boolean showResultDialog, DialogListener dialogListener) {
        activityCodeAuth = activityCode;
        dialogListenerAuth = dialogListener;
        Intent auth = IntentCreator.createAuth(activity, clientId, redirectUri, permissions, showResultDialog);
        activity.startActivityForResult(auth, activityCodeAuth);
    }
    
    public void showHistory(Activity activity, int activityCode, String accessToken, DialogListener dialogListener) {
        activityCodeHistory = activityCode;
        dialogListenerHistory = dialogListener;
        Intent history = IntentCreator.createHistory(activity, clientId, accessToken);
        activity.startActivityForResult(history, activityCodeHistory);
    }

    public void callbackOnResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == activityCodeAuth) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isSuccess = data.getBooleanExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);                                                
                if (isSuccess)
                    dialogListenerAuth.onSuccess(data.getExtras());
                else {
                    Exception e = (Exception) data.getSerializableExtra(ActivityParams.AUTH_OUT_EXCEPTION);                    
                    if (e == null) {
                        String error = data.getStringExtra(ActivityParams.AUTH_OUT_ERROR);
                        dialogListenerAuth.onFail(error);
                    } else {
                        dialogListenerAuth.onException(e);
                    }
                }
            } else {
                dialogListenerAuth.onCancel();
                
            }
        }
        
        if (requestCode == activityCodeHistory) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isSuccess = data.getBooleanExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
                if (isSuccess)
                    dialogListenerHistory.onSuccess(data.getExtras());
                else {
                    Exception e = (Exception) data.getSerializableExtra(ActivityParams.AUTH_OUT_EXCEPTION);
                    if (e == null) {
                        String error = data.getStringExtra(ActivityParams.AUTH_OUT_ERROR);
                        dialogListenerHistory.onFail(error);
                    } else {
                        dialogListenerHistory.onException(e);
                    }                    
                }
            } else {
                dialogListenerHistory.onCancel();    
            }
        }
    }

    public static interface DialogListener {

        public void onSuccess(Bundle values);

        public void onFail(String cause);

        public void onException(Exception exception);

        public void onCancel();
    }
}
