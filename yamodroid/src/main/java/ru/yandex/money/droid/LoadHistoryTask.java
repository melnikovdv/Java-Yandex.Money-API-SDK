package ru.yandex.money.droid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.response.util.Operation;

import java.io.IOException;
import java.util.List;

/**
 * @author dvmelnikov
 */

class LoadHistoryTask extends AsyncTask<Integer, Void, List<Operation>> {

    private final Context context;
    private String error;
    private String clientId;
    private final String accessToken;
    private final HistoryAdapter historyAdapter;

    ProgressDialog dialog;

    public LoadHistoryTask(Context context, String clientId, String accessToken,
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
    protected void onPostExecute(List<Operation> result) {
        if (result != null) {
            for (Operation op : result)
                historyAdapter.add(op);
        } else
            Utils.showError(context, "Ошибка: " + error);
        dialog.dismiss();
    }

    @Override
    protected List<Operation> doInBackground(Integer... params) {
        YandexMoney ym = Utils.getYandexMoney(clientId);
        try {
            OperationHistoryResponse resp =
                    ym.operationHistory(accessToken, params[0]);
            if (resp.isSuccess())
                return resp.getOperations();
            else
                error = resp.getError();

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
