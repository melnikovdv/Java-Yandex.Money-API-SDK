package ru.yandex.money.droid.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.enums.MoneyDirection;
import ru.yandex.money.api.response.OperationDetailResponse;
import ru.yandex.money.droid.R;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.utils.Utils;

import java.io.IOException;

/**
 * @author dvmelnikov
 */

public class DetailHistoryActivity extends Activity {

    private DetailHistoryActivity context;
    private TextView title;
    private TextView sum;
    private TextView date;
    private TextView details;
    private TextView message;
    private TextView accCaption;
    private TextView acc;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(LibConsts.PREF_CLIENT_ID);
        
        setContentView(R.layout.ymd_detail);
        context = this;        

        title = (TextView) findViewById(R.id.tvTitle);
        title.setText("");
        sum = (TextView) findViewById(R.id.tvSum);
        sum.setText("");
        date = (TextView) findViewById(R.id.tvDate);
        date.setText("");
        details = (TextView) findViewById(R.id.tvDetails);
        details.setText("");
        message = (TextView) findViewById(R.id.tvMessage);
        message.setText("");
        accCaption = (TextView) findViewById(R.id.tvAccountCaption);
        accCaption.setText("");
        acc = (TextView) findViewById(R.id.tv_send_to);
        acc.setText("");

        String opId = getIntent().getExtras().getString(LibConsts.OPERATION_ID);
        new LoadDetailTask().execute(opId);
    }

    public class LoadDetailTask extends AsyncTask<String, Void, OperationDetailResponse> {

        private ProgressDialog dialog;
        private String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(context, LibConsts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(OperationDetailResponse resp) {
            if (resp != null) {
                title.setText(resp.getTitle());
                if (resp.getDirection() == MoneyDirection.in) {
                    sum.setText("+" + resp.getAmount());
                    accCaption.setText("Отправитель:");
                    acc.setText(resp.getSender());

                } else {
                    sum.setText("-" + resp.getAmount());
                    accCaption.setText("Получатель:");
                    acc.setText(resp.getRecipient());
                }
                date.setText(resp.getDatetime().toLocaleString());
                details.setText(resp.getDetails());
                message.setText(resp.getMessage());
            } else
                Utils.showError(context, error);
            dialog.dismiss();
        }

        @Override
        protected OperationDetailResponse doInBackground(String... params) {
            YandexMoney ym = Utils.getYandexMoney(context);
            try {
                OperationDetailResponse resp =
                        ym.operationDetail(Utils.getToken(context, clientId), params[0]);
                if (resp.isSuccess())
                    return resp;
                else
                    error = "Ошибка: " + resp.getError();
            } catch (IOException e) {
                error = "Ошибка: " + e.getMessage();
            } catch (InvalidTokenException e) {
                error = "Ошибка: " + e.getMessage();
            } catch (InsufficientScopeException e) {
                error = "Ошибка: " + e.getMessage();
            }
            return null;
        }
    }
}
