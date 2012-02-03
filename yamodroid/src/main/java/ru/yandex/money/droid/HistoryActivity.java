package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import ru.yandex.money.api.response.util.Operation;

import java.util.LinkedList;

/**
 * @author dvmelnikov
 */

public class HistoryActivity extends Activity {

    public static final String HISTORY_IN_CLIENT_ID = "client_id";
    public static final String HISTORY_IN_ACCESS_TOKEN = "access_token";

    private String accessToken;
    private String clientId;

    private ImageView imgRefresh;
    private ListView listView;
    private HistoryAdapter historyAdapter;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityParams.HISTORY_OUT_IS_SUCCESS, true);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(HISTORY_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(HISTORY_IN_ACCESS_TOKEN);

        setContentView(R.layout.ymd_history);

        imgRefresh = (ImageView) findViewById(R.id.image_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                historyAdapter.clear();
                new LoadHistoryTask(HistoryActivity.this, clientId, accessToken,
                        historyAdapter)
                        .execute(0);
            }
        });

        listView = (ListView) findViewById(R.id.list_history);
        historyAdapter =
                new HistoryAdapter(this, R.layout.ymd_history_item,
                        new LinkedList<Operation>(), clientId, accessToken);

        listView.setAdapter(historyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position,
                    long id) {
                Operation operation =
                        (Operation) historyAdapter.getItem(position);
                Intent intent = new Intent(HistoryActivity.this, DetailHistoryActivity.class);
                intent.putExtra(DetailHistoryActivity.DET_HIST_IN_OPERATION_ID,
                        operation.getOperationId());
                intent.putExtra(DetailHistoryActivity.DET_HIST_IN_ACCESS_TOKEN,
                        accessToken);
                intent.putExtra(DetailHistoryActivity.DET_HIST_IN_CLIENT_ID,
                        clientId);
                startActivity(intent);
            }
        });

        new LoadHistoryTask(this, clientId, accessToken, historyAdapter).execute(0);
    }

}
