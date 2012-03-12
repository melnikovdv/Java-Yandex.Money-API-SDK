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

    public YandexMoneyDroid(String clientId) {
        this.clientId = clientId;
    }

    public void authorize(Activity activity, int activityCode, String redirectUri,
                          Collection<Permission> permissions, boolean showResultDialog) {
        Intent auth = IntentCreator.createAuth(activity, clientId, redirectUri, permissions, showResultDialog);
        activity.startActivityForResult(auth, activityCode);
    }

    public void authorizeCallback(int requestCode, int resultCode, Intent data) {

    }

    public static interface DialogListener {

        public void onSuccess(Bundle values);
        
        public void onFail(String cause);

        public void onError(Throwable exception);               
      
        public void onCancel();
    }
}
