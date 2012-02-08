package com.samples.yamodroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.AccountInfoResponse;
import ru.yandex.money.droid.ActivityParams;
import ru.yandex.money.droid.IntentCreator;
import ru.yandex.money.droid.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

public class GreatAppActivity extends Activity {

    private final int CODE_AUTH = 1710;
    private final int CODE_PAYMENT_P2P = 1720;
    private final int CODE_PAYMENT_SHOP = 1730;

    private final String TAG = "yamodroid-sample";
    private static final String PREFERENCES = "yandex_money_preferences";
    private final String PREF_ACCESS_TOKEN = "access_token";

    private TextView tvAuthStatus;
    private TextView tvAccount;
    private TextView tvBalance;
    private TextView tvCurrency;
    private Button btnAuth;
    private LinearLayout llFuncs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        llFuncs = (LinearLayout) findViewById(R.id.ll_funcs);

        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Если не авторизован, то авторизуемся по нажатию
                if (!isAuthorized(false)) {
                    Intent intent = IntentCreator.createAuth(
                            GreatAppActivity.this,
                            Consts.CLIENT_ID,
                            Consts.REDIRECT_URI,
                            Consts.getPermissions(),
                            true);
                    startActivityForResult(intent, CODE_AUTH);
                } else { // Если уже авторизован, то выходим
                    storeToken("");
                }
                refresh();
            }
        });

        Button btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isAuthorized(true))
                    return;

                Intent historyIntent = IntentCreator.createHistory(
                        GreatAppActivity.this, Consts.CLIENT_ID,
                        restoreToken());
                startActivity(historyIntent);
            }
        });

        Button btnPaymentP2P = (Button) findViewById(R.id.btnP2p);
        btnPaymentP2P.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isAuthorized(true))
                    return;

                Intent intent = IntentCreator
                        .createPaymentP2P(GreatAppActivity.this,
                                Consts.CLIENT_ID, restoreToken(),
                                "410011161616877", BigDecimal.valueOf(0.02),
                                "comment for p2p", "message for p2p", true);

                startActivityForResult(intent, CODE_PAYMENT_P2P);
            }
        });

        Button btnPaymentShop = (Button) findViewById(R.id.btnShop);
        btnPaymentShop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isAuthorized(true))
                    return;

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("PROPERTY1", "921");
                params.put("PROPERTY2", "3020052");
                params.put("sum", "1.00");

                Intent intent = IntentCreator.createPaymentShop(
                        GreatAppActivity.this, Consts.CLIENT_ID, restoreToken(),
                        BigDecimal.valueOf(1.00), "337", params, true);

                startActivityForResult(intent, CODE_PAYMENT_SHOP);
            }
        });

        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);

        tvAuthStatus = (TextView) findViewById(R.id.tvAuthStatus);
        renewAuthCaptions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        visibleFunctions();
        renewAuthCaptions();
        renewAccountInfo();
    }

    private void visibleFunctions() {
        if (isAuthorized(false)) 
            llFuncs.setVisibility(View.VISIBLE);
        else
            llFuncs.setVisibility(View.GONE);
    }

    private void renewAccountInfo() {
        new LoadAccountInfoTask().execute(null);
    }

    private void renewAuthCaptions() {
        if (isAuthorized(false)) {
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

        if (requestCode == CODE_AUTH) {
            boolean isSuccess =
                    data.getBooleanExtra(ActivityParams.AUTH_OUT_IS_SUCCESS,
                            false);
            String token =
                    data.getStringExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN);
            String error = data.getStringExtra(ActivityParams.AUTH_OUT_ERROR);

            if (isSuccess)
                storeToken(token);

            Toast.makeText(this,
                    "Authorization result: " + isSuccess + "\ntoken: " + token + "\n" +
                            "error: " + error, Toast.LENGTH_LONG).show();
        }

        if (requestCode == CODE_PAYMENT_P2P) {
            boolean isSuccess =
                    data.getBooleanExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                            false);
            String paymentId =
                    data.getStringExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID);
            String error = data.getStringExtra(ActivityParams.PAYMENT_OUT_ERROR);

            Toast.makeText(this,
                    "P2P payment result: " + isSuccess + "\npayment_id: " + paymentId + "\n" +
                            "error: " + error, Toast.LENGTH_LONG).show();
        }
        
        if (requestCode == CODE_PAYMENT_SHOP) {
            boolean isSuccess =
                    data.getBooleanExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                            false);
            String paymentId =
                    data.getStringExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID);
            String error = data.getStringExtra(ActivityParams.PAYMENT_OUT_ERROR);

            Toast.makeText(this,
                    "Shop payment result: " + isSuccess + "\npayment_id: " + paymentId + "\n" +
                            "error: " + error, Toast.LENGTH_LONG).show();
        }
    }

    private void storeToken(String token) {
        SharedPreferences sp = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        sp.edit().putString(PREF_ACCESS_TOKEN, token).commit();
    }

    private String restoreToken() {
        SharedPreferences sp = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return sp.getString(PREF_ACCESS_TOKEN, "");
    }

    private boolean isAuthorized(boolean showMessage) {
        if (restoreToken().equals("")) {
            if (showMessage)
                Toast.makeText(GreatAppActivity.this, "Не авторизован!",
                        Toast.LENGTH_LONG).show();
            return false;
        } else
        return true;
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
            } else {
                tvAccount.setText("");
                tvBalance.setText("");
                tvCurrency.setText("");
            }
        }

        @Override
        protected AccountInfoResponse doInBackground(Void... params) {
            try {
                if (isAuthorized(false)) {
                    YandexMoney ym = Utils.getYandexMoney(Consts.CLIENT_ID);
                    return ym.accountInfo(restoreToken());
                }
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

