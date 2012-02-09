package ru.yandex.money.droid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.enums.MoneyDirection;
import ru.yandex.money.api.response.OperationDetailResponse;

import java.io.IOException;
import java.text.DateFormat;

/**
 * @author dvmelnikov
 */

public class DetailHistoryActivity extends Activity {

    public static final String DET_HIST_IN_CLIENT_ID = "client_id";
    public static final String DET_HIST_IN_ACCESS_TOKEN = "access_token";
    public static final String DET_HIST_IN_OPERATION_ID = "operation_id";

    private DetailHistoryActivity context;
    private String clientId;
    private String accessToken;
    private String operationId;

    private TextView title;
    private TextView sum;
    private TextView date;
    private TextView details;
    private TextView message;
    private TextView accCaption;
    private TextView acc;
    private TextView direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ymd_detail);
        context = this;

        clientId = getIntent().getStringExtra(DET_HIST_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(DET_HIST_IN_ACCESS_TOKEN);
        operationId = getIntent().getStringExtra(DET_HIST_IN_OPERATION_ID);

        title = (TextView) findViewById(R.id.tvTitle);
        title.setText("");
        sum = (TextView) findViewById(R.id.tvSum);
        sum.setText("");
        direction = (TextView) findViewById(R.id.tv_direction);
        direction.setText("");
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

        new LoadDetailTask().execute(operationId);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, true);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public class LoadDetailTask extends AsyncTask<String, Void, OperationDetailResponse> {

        private ProgressDialog dialog;
        private String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(context, Consts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(OperationDetailResponse resp) {
            if ((resp == null) || (error != null)) {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.HISTORY_OUT_ERROR, error);
                context.setResult(Activity.RESULT_CANCELED, intent);
                context.finish();
            } else {
                if (resp.isSuccess()) {
                    title.setText(resp.getTitle());
                    if (resp.getDirection() == MoneyDirection.in) {
                        sum.setText(resp.getAmount().toString());
                        direction.setText("приход");
                        accCaption.setText("Отправитель:");
                        acc.setText(resp.getSender());

                    } else {
                        sum.setText(resp.getAmount().toString());
                        direction.setText("расход");
                        accCaption.setText("Получатель:");
                        acc.setText(resp.getRecipient());
                    }

                    String df = DateFormat.getDateInstance().format(resp.getDatetime());
                    date.setText(df);
                    details.setText(resp.getDetails());
                    message.setText(resp.getMessage());
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
        protected OperationDetailResponse doInBackground(String... params) {
            YandexMoney ym = Utils.getYandexMoney(clientId);
            try {
                OperationDetailResponse resp =
                        ym.operationDetail(accessToken, params[0]);
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
