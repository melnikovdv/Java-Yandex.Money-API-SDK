package ru.yandex.money.droid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.response.util.Operation;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author dvmelnikov
 */

public class HistoryActivity extends Activity {

    public static final String HISTORY_IN_CLIENT_ID = "client_id";
    public static final String HISTORY_IN_ACCESS_TOKEN = "access_token";

    private String accessToken;
    private String clientId;
    private int DETAIL_HISTORY_ACTIVITY_CODE = 4867943;
    
    private ImageView imgRefresh;
    private ListView listView;
    private HistoryAdapter historyAdapter;
    private YandexMoneyDroid ymd;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, true);
        this.setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(HISTORY_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(HISTORY_IN_ACCESS_TOKEN);
        ymd = new YandexMoneyDroid(clientId);

        setContentView(R.layout.ymd_history);

        imgRefresh = (ImageView) findViewById(R.id.image_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                historyAdapter.clear();                
                new LoadHistoryTask(HistoryActivity.this, clientId, accessToken, historyAdapter, new OnLoadNewCancel()).execute(0);
            }
        });

        listView = (ListView) findViewById(R.id.list_history);
        historyAdapter =
                new HistoryAdapter(this, R.layout.ymd_history_item,
                        new LinkedList<Operation>(), clientId, accessToken, new OnLoadNewCancel());

        listView.setAdapter(historyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Operation operation = (Operation) historyAdapter.getItem(position);

                ymd.showHistoryDetail(HistoryActivity.this, DETAIL_HISTORY_ACTIVITY_CODE, accessToken,
                        operation.getOperationId(), new YandexMoneyDroid.DialogListener() {
                    @Override
                    public void onSuccess(Bundle values) {
                        Intent intent = new Intent();
                        intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, true);
                        HistoryActivity.this.setResult(Activity.RESULT_OK, intent);
                        HistoryActivity.this.finish();
                    }

                    @Override
                    public void onFail(String cause) {
                        Toast.makeText(HistoryActivity.this, "Ошибка запроса деталей операции: " + cause,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Exception exception) {
                        String text = null;
                        if (exception.getClass() == InsufficientScopeException.class)
                            text = "Ошибка: нет прав для просмотра детальной информации";
                        if (exception.getClass() == IOException.class)
                            text = "Ошибка связи: проверьте подключение к интернету";
                        if (exception.getClass() == InvalidTokenException.class)
                            text = "Ошибка: невалидный токен";

                        if (text != null) {
                            Toast.makeText(HistoryActivity.this, text,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        new LoadHistoryTask(this, clientId, accessToken, historyAdapter, new OnStartupCancel()).execute(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ymd.callbackOnResult(requestCode, resultCode, data);
    }
    
    class OnStartupCancel implements DialogInterface.OnCancelListener {
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, false);
            HistoryActivity.this.setResult(Activity.RESULT_CANCELED, intent);
            HistoryActivity.this.finish();
        }
    }

    class OnLoadNewCancel implements DialogInterface.OnCancelListener {

        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
        }
    }
}
