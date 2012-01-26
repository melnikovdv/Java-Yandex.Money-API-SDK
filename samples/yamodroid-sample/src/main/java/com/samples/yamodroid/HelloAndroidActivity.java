package com.samples.yamodroid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.response.AccountInfoResponse;
import ru.yandex.money.droid.YandexMoneyDroid;
import ru.yandex.money.droid.YandexMoneyDroidImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

public class HelloAndroidActivity extends Activity {

    private static String TAG = "yamodroid-sample";

    private YandexMoneyDroid ymd;
    private TextView tvAuthStatus;
    private Button btnAuth;
    private Button btnHistory;
    private Button btnP2p;
    private Button btnShop;
    private TextView tvAccount;
    private TextView tvBalance;
    private TextView tvCurrency;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.main);

        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnP2p = (Button) findViewById(R.id.btnP2p);
        btnShop = (Button) findViewById(R.id.btnShop);

        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);

        ymd = new YandexMoneyDroidImpl(this, Consts.CLIENT_ID, Consts.REDIRECT_URI);

        tvAuthStatus = (TextView) findViewById(R.id.tvAuthStatus);
        renewAuthCaptions();

        btnAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ymd.hasToken()) {
                    ymd.unauthorize();
                    tvAccount.setText("");
                    tvBalance.setText("");
                    tvCurrency.setText("");
                } else
                    ymd.authorize(Consts.getPermissions());
                renewAuthCaptions();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ymd.operationHistoryActivity();
            }
        });

        btnP2p.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ymd.requestPaymentP2P("410011161616877",
                        BigDecimal.valueOf(0.02),
                        "comment for p2p", "message for p2p");
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("PROPERTY1", "921");
                params.put("PROPERTY2", "3020052");
                params.put("sum", "1.00");
                ymd.requestPaymentShop(BigDecimal.valueOf(1.00), "337", params,
                        true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ymd != null) {
            renewAuthCaptions();
            renewAccountInfo();
        }
    }

    private void renewAccountInfo() {
        new LoadAccountInfoTask().execute(null);
    }

    private void renewAuthCaptions() {

        if (ymd.hasToken()) {
            tvAuthStatus.setText("Статус: авторизован");
            btnAuth.setText("Выйти");
        } else {
            tvAuthStatus.setText("Статус: не авторизован");
            btnAuth.setText("Авторизоваться");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {

        if (requestCode == ru.yandex.money.droid.Consts.CODE_AUTH) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Payment auth: ok", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == ru.yandex.money.droid.Consts.CODE_PAYMENT_P2P) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Payment p2p: ok", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == ru.yandex.money.droid.Consts.CODE_PAYMENT_SHOP) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Payment shop: ok", Toast.LENGTH_LONG).show();
            }
        }

    }

    class LoadAccountInfoTask extends
            AsyncTask<Void, Void, AccountInfoResponse> {

        @Override
        protected void onPostExecute(AccountInfoResponse air) {
            if (air != null) {
                tvAccount.setText(air.getAccount());
                tvBalance.setText(air.getBalance().toString());
                if (air.getCurrency().equals("643"))
                    tvCurrency.setText("рубль");
            }
        }

        @Override
        protected AccountInfoResponse doInBackground(Void... params) {
            try {
                if (ymd.hasToken())
                    return ymd.accountInfo();
            } catch (InsufficientScopeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvalidTokenException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }
    }
}

