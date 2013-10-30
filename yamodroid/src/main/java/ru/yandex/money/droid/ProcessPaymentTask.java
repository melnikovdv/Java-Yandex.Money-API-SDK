package ru.yandex.money.droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import ru.yandex.money.api.ApiCommandsFacade;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.response.ProcessPaymentResponse;

import java.io.IOException;

/**
 * User: mdv
 * Date: 19.03.12
 * Time: 20:54
 */
class ProcessPaymentTask extends AsyncTask<Void, Void, ProcessPaymentTask.ProcessPaymentResp> {

    ProgressDialog dialog;
    private Activity activity;
    private String clientId;
    private String accessToken;
    private String requestId;
    private MoneySource moneySource;
    private boolean showResultDialog;
    private String cvc;

    ProcessPaymentTask(Activity activity, String clientId, String accessToken, String requestId,
                       MoneySource moneySource, boolean showResultDialog, String cvc) {

        this.activity = activity;
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.requestId = requestId;
        this.moneySource = moneySource;
        this.showResultDialog = showResultDialog;
        this.cvc = cvc;
    }

    @Override
    protected void onPreExecute() {
        dialog = Utils.makeProgressDialog(activity, "Выполнение платежа", Consts.WAIT);
        dialog.setCancelable(false);
        if (!activity.isFinishing())
            dialog.show();
    }

    @Override
    protected ProcessPaymentResp doInBackground(
            Void... params) {
        AndroidHttpClient client = Utils.httpClient();
        try {
            ApiCommandsFacade ym = Utils.getYandexMoney(clientId, client);
            ProcessPaymentResponse resp = null;
            if (moneySource == MoneySource.wallet)
                resp = ym.processPaymentByWallet(accessToken, requestId);
            if (moneySource == MoneySource.card)
                resp = ym.processPaymentByCard(accessToken, requestId, cvc);
            return new ProcessPaymentResp(resp, null);
        } catch (IOException e) {
            return new ProcessPaymentResp(null, e);
        } catch (InsufficientScopeException e) {
            return new ProcessPaymentResp(null, e);
        } catch (InvalidTokenException e) {
            return new ProcessPaymentResp(null, e);
        } finally {
            client.close();
        }
    }

    @Override
    protected void onPostExecute(ProcessPaymentResp resp) {
        dialog.dismiss();

        if (resp.getException() == null) {
            if (resp.getResponse().isSuccess()) {
                if (showResultDialog) {
                    if (!activity.isFinishing())
                        makeResultAlertDialog(true, null, resp.getResponse().getPaymentId(), "Перевод").show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, true);
                    intent.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID, resp.getResponse().getPaymentId());
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, resp.getResponse().getError());
                activity.setResult(Activity.RESULT_CANCELED, intent);
                activity.finish();
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
            intent.putExtra(ActivityParams.PAYMENT_OUT_EXCEPTION, resp.getException());
            activity.setResult(Activity.RESULT_CANCELED, intent);
            activity.finish();
        }
    }

    private AlertDialog makeResultAlertDialog(final boolean isSuccess,
                                              final String error, final String operationId, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_wallet);
        if (title != null)
            builder.setTitle(title);
        if (isSuccess)
            builder.setMessage("Перевод успешно завершен");
        else
            builder.setMessage("Ошибка: " + error);

        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent authResult = new Intent();
                authResult.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                        isSuccess);
                if (error != null)
                    authResult.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                if (operationId != null)
                    authResult.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID, operationId);

                if (isSuccess)
                    activity.setResult(Activity.RESULT_OK, authResult);
                else
                    activity.setResult(Activity.RESULT_CANCELED, authResult);
                activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public class ProcessPaymentResp {
        private ProcessPaymentResponse response;
        private Exception exception;

        private ProcessPaymentResp(ProcessPaymentResponse response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        public ProcessPaymentResponse getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }
}
