package com.samples.yamodroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
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
import ru.yandex.money.droid.Utils;
import ru.yandex.money.droid.YandexMoneyDroid;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

public class GreatAppActivity extends Activity {

    private final int CODE_AUTH = 1710;
    private final int CODE_PAYMENT_P2P = 1720;
    private final int CODE_PAYMENT_SHOP = 1730;
    private static final int CODE_HISTORY = 1740;

    private static final String PREFERENCES = "yandex_money_preferences";
    private final String PREF_ACCESS_TOKEN = "access_token";

    private TextView tvAuthStatus;
    private TextView tvAccount;
    private TextView tvBalance;
    private TextView tvCurrency;
    private Button btnAuth;
    private LinearLayout llFuncs;

    private YandexMoneyDroid ymd = new YandexMoneyDroid(Consts.CLIENT_ID);
    private MyDialogListener dialogListener = new MyDialogListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        llFuncs = (LinearLayout) findViewById(R.id.ll_funcs);

        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                if (!isAuthorized(false)) {
                    ymd.authorize(GreatAppActivity.this, CODE_AUTH, Consts.REDIRECT_URI, Consts.getPermissions(), true,
                            dialogListener);
                } else {
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
                ymd.showHistory(GreatAppActivity.this, CODE_HISTORY, restoreToken(), dialogListener);
            }
        });

        Button btnPaymentP2P = (Button) findViewById(R.id.btnP2p);
        btnPaymentP2P.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isAuthorized(true))
                    return;

                ymd.showPaymentP2P(GreatAppActivity.this, CODE_PAYMENT_P2P, restoreToken(),
                        "410011161616877", BigDecimal.valueOf(0.02), "comment for p2p",
                        "message for p2p", true, dialogListener);
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

                ymd.showPaymentShop(GreatAppActivity.this, CODE_PAYMENT_SHOP, restoreToken(), BigDecimal.valueOf(1.00),
                        "337", params, true, dialogListener);
            }
        });

        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);

        tvAuthStatus = (TextView) findViewById(R.id.tvAuthStatus);
        renewAuthCaptions();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        ymd.callbackOnResult(requestCode, resultCode, data);
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
        new LoadAccountInfoTask().execute();
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

    class LoadAccountInfoTask extends AsyncTask<Void, Void, AccountInfoResponse> {

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
            AndroidHttpClient client = Utils.httpClient();
            try {
                if (isAuthorized(false)) {
                    YandexMoney ym = Utils.getYandexMoney(Consts.CLIENT_ID, client);
                    return ym.accountInfo(restoreToken());
                }
            } catch (InsufficientScopeException e) {
                // do new authorization with sufficient permissions scope
            } catch (InvalidTokenException e) {
                // do auth again
            } catch (IOException e) {
                // there is no internet ;-(
            } finally {
                client.close();
            }
            return null;
        }
    }

    private class MyDialogListener implements YandexMoneyDroid.DialogListener {
        public void onSuccess(Bundle values) {
            String token = values.getString(ActivityParams.AUTH_OUT_ACCESS_TOKEN);
            if (token != null) {
                storeToken(token);
                Toast.makeText(GreatAppActivity.this, "Success. Access token = " + token, Toast.LENGTH_LONG).show();
            }

            String paymentId = values.getString(ActivityParams.PAYMENT_OUT_OPERATION_ID);
            if (paymentId != null) {
                Toast.makeText(GreatAppActivity.this, "payment successfully finished. Payment id: " + paymentId, Toast.LENGTH_LONG).show();
            }
        }

        public void onFail(String cause) {
            Toast.makeText(GreatAppActivity.this, "Fail: " + cause, Toast.LENGTH_LONG).show();
        }

        public void onException(Exception exception) {
            Toast.makeText(GreatAppActivity.this, "Exception: " + exception.getClass().getName(), Toast.LENGTH_LONG)
                    .show();            
        }

        public void onCancel() {
            Toast.makeText(GreatAppActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
        }
    }

}

