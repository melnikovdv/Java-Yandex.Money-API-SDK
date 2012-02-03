package ru.yandex.money.droid;

import android.app.Activity;
import android.app.ProgressDialog;
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

class LoadHistoryTask extends AsyncTask<Integer, Void, OperationHistoryResponse> {

    private final Activity context;
    private String error;
    private String clientId;
    private final String accessToken;
    private final HistoryAdapter historyAdapter;

    ProgressDialog dialog;

    public LoadHistoryTask(Activity context, String clientId, String accessToken,
            HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
        this.context = context;
        this.clientId = clientId;
        this.accessToken = accessToken;
    }

    @Override
    protected void onPreExecute() {
        dialog = Utils.makeProgressDialog(context, Consts.WAIT);
        dialog.show();
    }

    @Override
    protected void onPostExecute(OperationHistoryResponse resp) {
        if ((resp == null) || (error != null)) {
            Intent intent = new Intent();
            intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
            intent.putExtra(ActivityParams.HISTORY_OUT_ERROR, error);
            context.setResult(Activity.RESULT_CANCELED, intent);
            context.finish();
        } else {
            if (resp.isSuccess()) {
                for (Operation op : resp.getOperations())
                    historyAdapter.add(op);
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.HISTORY_OUT_ERROR, resp.getError());
                context.setResult(Activity.RESULT_CANCELED, intent);
                context.finish();
            }
        }

        dialog.dismiss();
    }

    @Override
    protected OperationHistoryResponse doInBackground(Integer... params) {
        YandexMoney ym = Utils.getYandexMoney(clientId);
        try {
            return ym.operationHistory(accessToken, params[0]);

        } catch (IOException e) {
            e.printStackTrace();
            error = e.getMessage();
        } catch (InvalidTokenException e) {
            e.printStackTrace();
            error = e.getMessage();
        } catch (InsufficientScopeException e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return null;
    }
}
