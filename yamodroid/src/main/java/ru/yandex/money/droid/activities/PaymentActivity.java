package ru.yandex.money.droid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.ProcessPaymentResponse;
import ru.yandex.money.api.response.RequestPaymentResponse;
import ru.yandex.money.droid.R;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.utils.RequestPaymentShopParcelable;
import ru.yandex.money.droid.utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author dvmelnikov
 */

public class PaymentActivity extends Activity {

    boolean paymentP2P;
    private Button btnSend;
    private String token;
    private YandexMoney ym;
    private TextView tvDescr;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clientId = getIntent().getStringExtra(LibConsts.PREF_CLIENT_ID);
        token = Utils.getToken(PaymentActivity.this, clientId);
        ym = Utils.getYandexMoney(PaymentActivity.this);
        paymentP2P = getIntent().getExtras().getBoolean(LibConsts.PAYMENT_P2P);
        
        if (paymentP2P) {
            setContentView(R.layout.ymd_payment_p2p);
            btnSend = (Button) findViewById(R.id.btn_send);

            TextView tvTo = (TextView) findViewById(R.id.tv_send_to);
            tvTo.setText("");
            TextView tvSum = (TextView) findViewById(R.id.tv_sum);
            tvSum.setText("");
            TextView tvComment = (TextView) findViewById(R.id.tv_comment);
            tvComment.setText("");
            TextView tvMessage = (TextView) findViewById(R.id.tv_message);
            tvMessage.setText("");

            String to = getIntent().getExtras().getString(LibConsts.PAYMENT_P2P_TO);
            Double sum = getIntent().getExtras().getDouble(
                    LibConsts.PAYMENT_P2P_SUM);
            String comment = getIntent().getExtras().getString(LibConsts.PAYMENT_P2P_COMMENT);
            String message = getIntent().getExtras().getString(
                    LibConsts.PAYMENT_P2P_MESSAGE);

            P2pParams params = new P2pParams(to, sum, comment, message);

            tvTo.setText(params.getTo());
            tvSum.setText(params.getSum().toString());
            tvComment.setText(params.getComment());
            tvMessage.setText(params.getMessage());

            new RequestPaymentP2pTask().execute(params);
        } else {
            setContentView(R.layout.ymd_payment_shop);
            btnSend = (Button) findViewById(R.id.btn_send);

            TextView tvSum = (TextView) findViewById(R.id.tv_sum);
            tvSum.setText("");
            tvDescr = (TextView) findViewById(R.id.tv_descr);
            tvDescr.setText("");

            RequestPaymentShopParcelable params = getIntent().getParcelableExtra(
                    LibConsts.PAYMENT_SHOP_PARC);
            tvSum.setText(params.getSum().toString());

            new RequestPaymentShopTask().execute(params);
        }
    }

    private class P2pParams {
        private final String to;
        private final Double sum;
        private final String comment;
        private final String message;

        private P2pParams(String to, Double sum, String comment, String message) {
            this.to = to;
            this.sum = sum;
            this.comment = comment;
            this.message = message;
        }

        public String getTo() {
            return to;
        }

        public Double getSum() {
            return sum;
        }

        public String getComment() {
            return comment;
        }

        public String getMessage() {
            return message;
        }
    }
    
    private class RequestPaymentP2pTask extends AsyncTask<P2pParams, Void, RequestPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this, "Подготовка перевода", LibConsts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResponse requestPaymentResponse) {
            dialog.dismiss();
            if (error != null) {
                btnSend.setEnabled(false);
                Utils.showError(PaymentActivity.this, "Ошибка: " + error);
            } else {
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new ProcessPaymentTask().execute(requestPaymentResponse.getRequestId());
                    }
                });
            }
        }

        @Override
        protected RequestPaymentResponse doInBackground(
                P2pParams... params) {
            try {
                RequestPaymentResponse resp = ym.requestPaymentP2P(token,
                        params[0].getTo(), BigDecimal.valueOf(params[0].getSum()),
                        params[0].getComment(), params[0].getMessage());
                if (resp.isSuccess()) {
                    return resp;
                } else {
                    error = resp.getError();
                    return null;
                }
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

    private class RequestPaymentShopTask extends AsyncTask<RequestPaymentShopParcelable, Void, RequestPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this, "Подготовка перевода", LibConsts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResponse requestPaymentResponse) {
            dialog.dismiss();
            if (error != null) {
                btnSend.setEnabled(false);
                Utils.showError(PaymentActivity.this, "Ошибка: " + error);
            } else {
                tvDescr.setText(requestPaymentResponse.getContract());

                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new ProcessPaymentTask().execute(requestPaymentResponse.getRequestId());
                    }
                });
            }
        }

        @Override
        protected RequestPaymentResponse doInBackground(
                RequestPaymentShopParcelable... params) {
            try {
                RequestPaymentResponse resp = ym.requestPaymentShop(token,
                        params[0].getPatternId(), params[0].getParams());
                if (resp.isSuccess()) {
                    return resp;
                } else {
                    error = resp.getError();
                    return null;
                }
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

    private class ProcessPaymentTask extends AsyncTask<String, Void, ProcessPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this, "Выполнение перевода", LibConsts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(
                ProcessPaymentResponse processPaymentResponse) {
                dialog.dismiss();
            if (error != null) {
                Utils.showError(PaymentActivity.this, error);
            } else {
                AlertDialog resDialog = Utils.makeResultAlertDialog(
                        PaymentActivity.this, "Перевод",
                        "Успешно завершен", processPaymentResponse.getError());
                resDialog.show();
            }
        }

        @Override
        protected ProcessPaymentResponse doInBackground(String... params) {
            try {
                return ym.processPaymentByWallet(token, params[0]);
            } catch (IOException e) {
                e.printStackTrace();  
                error = e.getMessage();
            } catch (InsufficientScopeException e) {
                e.printStackTrace();
                error = e.getMessage();
            } catch (InvalidTokenException e) {
                e.printStackTrace();
                error = e.getMessage();
            }
            return null;
        }

    }
}

