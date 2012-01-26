package ru.yandex.money.droid.activities.history;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.response.util.Operation;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * @author dvmelnikov
 */

public class LoadHistoryTask extends AsyncTask<Integer, Void, List<Operation>> {

    ProgressDialog dialog;
    private final String token;
    private final HistoryAdapter historyAdapter;
    private final Context context;
    private String error;

    public LoadHistoryTask(Context context, HistoryAdapter historyAdapter, String clientId) {
        this.historyAdapter = historyAdapter;
        this.context = context;
        token = Utils.getToken(context, clientId);
    }

    @Override
    protected void onPreExecute() {
        dialog = Utils.makeProgressDialog(context, LibConsts.WAIT);
        dialog.show();
    }

    @Override
    protected void onPostExecute(List<Operation> result) {
        if (result != null) {
            for (Operation op : result)
                historyAdapter.add(op);
        } else
            Utils.showError(context, error);
        dialog.dismiss();
    }

    @Override
    protected List<Operation> doInBackground(Integer... params) {
        YandexMoney ym = Utils.getYandexMoney(context);
        try {
            OperationHistoryResponse resp =
                    ym.operationHistory(token, params[0]);
            if (resp.isSuccess())
                return resp.getOperations();
            else
                error = "Ошибка: " + resp.getError();

        } catch (IOException e) {
            e.printStackTrace();
            error = "Ошибка: " + e.getMessage();
        } catch (InvalidTokenException e) {
            e.printStackTrace();
            error = "Ошибка: " + e.getMessage();
        } catch (InsufficientScopeException e) {
            e.printStackTrace();
            error = "Ошибка: " + e.getMessage();
        }
        return null;
    }
}
