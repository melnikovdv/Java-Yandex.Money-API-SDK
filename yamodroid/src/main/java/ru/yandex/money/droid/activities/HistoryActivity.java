package ru.yandex.money.droid.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import ru.yandex.money.api.response.util.Operation;
import ru.yandex.money.droid.R;
import ru.yandex.money.droid.activities.history.HistoryAdapter;
import ru.yandex.money.droid.activities.history.LoadHistoryTask;
import ru.yandex.money.droid.preferences.LibConsts;

import java.util.LinkedList;

/**
 * @author dvmelnikov
 */

public class HistoryActivity extends Activity {


    private ImageView imgRefresh;
    private ListView listView;
    private HistoryAdapter historyAdapter;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(LibConsts.PREF_CLIENT_ID);

        setContentView(R.layout.ymd_history);

        imgRefresh = (ImageView) findViewById(R.id.image_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                historyAdapter.clear();
                new LoadHistoryTask(HistoryActivity.this, historyAdapter,
                        clientId)
                        .execute(0);
            }
        });

        listView = (ListView) findViewById(R.id.list_history);
        historyAdapter =
                new HistoryAdapter(this, R.layout.ymd_history_item,
                        new LinkedList<Operation>(), clientId);

        listView.setAdapter(historyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position,
                    long id) {
                Operation operation =
                        (Operation) historyAdapter.getItem(position);
                Intent intent = new Intent(HistoryActivity.this, DetailHistoryActivity.class);
                intent.putExtra(LibConsts.OPERATION_ID, operation.getOperationId());
                intent.putExtra(LibConsts.PREF_CLIENT_ID, clientId);
                startActivity(intent);
            }
        });

        new LoadHistoryTask(this, historyAdapter, clientId).execute(0);
    }

}
