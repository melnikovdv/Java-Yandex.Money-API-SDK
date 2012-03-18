package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ru.yandex.money.api.rights.Permission;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

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

    private int activityCodeHistoryDetail;
    private DialogListener dialogListenerHistoryDetail;

    private int activityCodePaymentP2P;
    private DialogListener dialogListenerPaymentP2P;
    private DialogListener dialogListenerPaymentShop;
    private int activityCodePaymentShop;

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

    public void showHistoryDetail(Activity activity, int activityCode, String accessToken, String operationId,
                                  DialogListener dialogListener) {
        activityCodeHistoryDetail = activityCode;
        dialogListenerHistoryDetail = dialogListener;
        Intent historyDetail = IntentCreator.createHistoryDetail(activity, clientId, accessToken, operationId);
        activity.startActivityForResult(historyDetail, activityCodeHistoryDetail);
    }

    public void showPaymentP2P(Activity activity, int activityCode, String accessToken, String accountTo,
                               BigDecimal amount, String comment, String message, boolean showResultDialog,
                               DialogListener dialogListener) {
        activityCodePaymentP2P = activityCode;
        dialogListenerPaymentP2P = dialogListener;
        Intent intent = IntentCreator.createPaymentP2P(activity, clientId, accessToken, accountTo, amount,
                comment, message, showResultDialog);
        activity.startActivityForResult(intent, activityCodePaymentP2P);
    }
    
    public void showPaymentShop(Activity activity, int activityCode, String accessToken, BigDecimal amount,
                                String patternId, HashMap<String, String> params, boolean showResultDialog, DialogListener dialogListener) {
        activityCodePaymentShop = activityCode;
        dialogListenerPaymentShop = dialogListener;
        Intent intent = IntentCreator.createPaymentShop(activity, clientId, accessToken, amount, patternId, params,
                showResultDialog);
        activity.startActivityForResult(intent, activityCodePaymentShop);
    }

    public void callbackOnResult(int requestCode, int resultCode, Intent data) {

        boolean isSuccess = data.getBooleanExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);

        if (requestCode == activityCodeAuth) {
            if (resultCode == Activity.RESULT_OK) {
                if (isSuccess)
                    dialogListenerAuth.onSuccess(data.getExtras());
            } else {
                Exception e = (Exception) data.getSerializableExtra(ActivityParams.AUTH_OUT_EXCEPTION);
                if (e == null) {
                    String error = data.getStringExtra(ActivityParams.AUTH_OUT_ERROR);
                    if (error == null) {
                        dialogListenerAuth.onCancel();
                    } else {
                        dialogListenerAuth.onFail(error);
                    }
                } else {
                    dialogListenerAuth.onException(e);
                }
            }
        }

        if (requestCode == activityCodeHistory) {
            if (resultCode == Activity.RESULT_OK) {
                if (isSuccess)
                    dialogListenerHistory.onSuccess(data.getExtras());
            } else {
                Exception e = (Exception) data.getSerializableExtra(ActivityParams.HISTORY_OUT_EXCEPTION);
                if (e == null) {
                    String error = data.getStringExtra(ActivityParams.HISTORY_OUT_ERROR);
                    if (error == null) {
                        dialogListenerHistory.onCancel();
                    } else {
                        dialogListenerHistory.onFail(error);
                    }
                } else {
                    dialogListenerHistory.onException(e);
                }
            }
        }

        if (requestCode == activityCodeHistoryDetail) {
            if (resultCode == Activity.RESULT_OK) {
                if (isSuccess)
                    dialogListenerHistoryDetail.onSuccess(data.getExtras());
            } else {
                Exception e = (Exception) data.getSerializableExtra(ActivityParams.HISTORY_DETAIL_OUT_EXCEPTION);
                if (e == null) {
                    String error = data.getStringExtra(ActivityParams.HISTORY_DETAIL_OUT_ERROR);
                    if (error == null) {
                        dialogListenerHistoryDetail.onCancel();
                    } else {
                        dialogListenerHistoryDetail.onFail(error);
                    }
                } else {
                    dialogListenerHistoryDetail.onException(e);
                }
            }
        }

        if (requestCode == activityCodePaymentP2P) {
            if (resultCode == Activity.RESULT_OK) {
                if (isSuccess)
                    dialogListenerPaymentP2P.onSuccess(data.getExtras());
            } else {
                Exception e = (Exception) data.getSerializableExtra(ActivityParams.PAYMENT_OUT_EXCEPTION);
                if (e == null) {
                    String error = data.getStringExtra(ActivityParams.PAYMENT_OUT_ERROR);
                    if (error == null) {
                        dialogListenerPaymentP2P.onCancel();
                    } else {
                        dialogListenerPaymentP2P.onFail(error);
                    }
                } else {
                    dialogListenerPaymentP2P.onException(e);
                }
            }
        }
        
        if (requestCode == activityCodePaymentShop) {
            if (resultCode == Activity.RESULT_OK) {
                if (isSuccess)
                    dialogListenerPaymentShop.onSuccess(data.getExtras());
            } else {
                Exception e = (Exception) data.getSerializableExtra(ActivityParams.PAYMENT_OUT_EXCEPTION);
                if (e == null) {
                    String error = data.getStringExtra(ActivityParams.PAYMENT_OUT_ERROR);
                    if (error == null) {
                        dialogListenerPaymentShop.onCancel();
                    } else {
                        dialogListenerPaymentShop.onFail(error);
                    }
                } else {
                    dialogListenerPaymentShop.onException(e);
                }
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
