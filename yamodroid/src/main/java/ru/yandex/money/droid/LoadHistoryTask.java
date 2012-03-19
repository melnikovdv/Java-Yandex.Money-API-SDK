package ru.yandex.money.droid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.response.util.Operation;

import java.io.IOException;

/**
 * @author dvmelnikov
 */

class LoadHistoryTask extends AsyncTask<Integer, Void, LoadHistoryTask.HistoryResp> {

    private final Activity context;
    private String error;
    private String clientId;
    private final String accessToken;
    private final HistoryAdapter historyAdapter;

    ProgressDialog dialog;
    private final DialogInterface.OnCancelListener onDialogCancelListener;

    public LoadHistoryTask(Activity context, String clientId, String accessToken, HistoryAdapter historyAdapter,
            DialogInterface.OnCancelListener onDialogCancelListener) {
        this.historyAdapter = historyAdapter;
        this.context = context;
        this.clientId = clientId;
        this.accessToken = accessToken;        
        this.onDialogCancelListener = onDialogCancelListener;
    }

    @Override
    protected void onPreExecute() {
        dialog = Utils.makeProgressDialog(context, Consts.WAIT);
        dialog.setOnCancelListener(onDialogCancelListener);
        if (!context.isFinishing())
            dialog.show();       
    }

    @Override
    protected void onPostExecute(HistoryResp resp) {                
        if (resp.getException() == null) {
            if (resp.getResponse().isSuccess()) {
                for (Operation op : resp.getResponse().getOperations())
                    historyAdapter.add(op);
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.HISTORY_OUT_ERROR, resp.getResponse().getError());
                context.setResult(Activity.RESULT_CANCELED, intent);
                context.finish();
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);            
            intent.putExtra(ActivityParams.HISTORY_OUT_EXCEPTION, resp.getException());
            context.setResult(Activity.RESULT_CANCELED, intent);
            context.finish();
        }               
        dialog.dismiss();
    }

    @Override
    protected HistoryResp doInBackground(Integer... params) {
        YandexMoney ym = Utils.getYandexMoney(clientId);
        try {
            OperationHistoryResponse resp = ym.operationHistory(accessToken, params[0]);
            return new HistoryResp(resp, null);            
        } catch (IOException e) {
            return new HistoryResp(null, e);
        } catch (InvalidTokenException e) {
            return new HistoryResp(null, e);
        } catch (InsufficientScopeException e) {
            return new HistoryResp(null, e);
        }        
    }

    class HistoryResp {        
        private final OperationHistoryResponse response;
        private final Exception exception;

        public HistoryResp(OperationHistoryResponse response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        public OperationHistoryResponse getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }
}
