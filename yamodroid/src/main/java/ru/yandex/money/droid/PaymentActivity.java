package ru.yandex.money.droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.enums.MoneySource;
import ru.yandex.money.api.response.ProcessPaymentResponse;
import ru.yandex.money.api.response.RequestPaymentResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author dvmelnikov
 */

public class PaymentActivity extends Activity {

    public static final String PAYMENT_IN_CLIENT_ID = "ru.yandex.money.droid.client_id";
    public static final String PAYMENT_IN_ACCESS_TOKEN = "ru.yandex.money.droid.access_token";
    public static final String PAYMENT_IN_P2P_FLAG = "ru.yandex.money.droid.p2p_flag";
    public static final String PAYMENT_IN_SHOW_RESULT_DIALOG =
            "ru.yandex.money.droid.show_result_dialog";

    public static final String PAYMENT_P2P_IN_ACCOUNT = "ru.yandex.money.droid.account";
    public static final String PAYMENT_P2P_IN_AMOUNT = "ru.yandex.money.droid.sum";
    public static final String PAYMENT_P2P_IN_COMMENT = "ru.yandex.money.droid.comment";
    public static final String PAYMENT_P2P_IN_MESSAGE = "ru.yandex.money.droid.message";

    public static final String PAYMENT_SHOP_IN_PARAMS = "ru.yandex.money.droid.parcelable_params";

    private String clientId;
    private String accessToken;
    private boolean showResultDialog;
    private boolean p2pFlag;

    private Button btnPayFromCard;
    private Button btnPayFromWallet;
    private TextView tvDescr;
    private LinearLayout layoutPaywithCard;
    private EditText edtCVC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (p2pFlag) {
            setContentView(R.layout.ymd_payment_p2p);
            btnPayFromCard = (Button) findViewById(R.id.btn_send_card);
            btnPayFromWallet = (Button) findViewById(R.id.btn_send_p2p);

            TextView tvTo = (TextView) findViewById(R.id.tv_send_to);
            tvTo.setText("");
            TextView tvSum = (TextView) findViewById(R.id.tv_sum);
            tvSum.setText("");
            TextView tvComment = (TextView) findViewById(R.id.tv_comment);
            tvComment.setText("");
            TextView tvMessage = (TextView) findViewById(R.id.tv_message);
            tvMessage.setText("");

            String to = getIntent().getStringExtra(PAYMENT_P2P_IN_ACCOUNT);
            Double sum = getIntent().getDoubleExtra(PAYMENT_P2P_IN_AMOUNT, 0.0);
            String comment = getIntent().getStringExtra(PAYMENT_P2P_IN_COMMENT);
            String message = getIntent().getStringExtra(PAYMENT_P2P_IN_MESSAGE);

            P2pParams params = new P2pParams(to, sum, comment, message);

            tvTo.setText(params.getTo());
            tvSum.setText(params.getSum().toString());
            tvComment.setText(params.getComment());
            tvMessage.setText(params.getMessage());

            new RequestPaymentP2pTask().execute(params);
        } else {
            setContentView(R.layout.ymd_payment_shop);
            btnPayFromCard = (Button) findViewById(R.id.btn_send_card);
            btnPayFromWallet = (Button) findViewById(R.id.btn_send_p2p);
            layoutPaywithCard =
                    (LinearLayout) findViewById(R.id.ll_pay_with_card);
            edtCVC = (EditText) findViewById(R.id.edt_cvc);

            TextView tvSum = (TextView) findViewById(R.id.tv_sum);
            tvSum.setText("");
            tvDescr = (TextView) findViewById(R.id.tv_descr);
            tvDescr.setText("");

            PaymentShopParcelable shopParams = getIntent().getParcelableExtra(PAYMENT_SHOP_IN_PARAMS);
            tvSum.setText(shopParams.getSum().toString());

            new RequestPaymentShopTask().execute(shopParams);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                false);
        intent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, Consts.USER_CANCELLED);
        this.setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void setupIntentParams() {
        clientId = getIntent().getStringExtra(PAYMENT_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(PAYMENT_IN_ACCESS_TOKEN);
        p2pFlag = getIntent().getExtras().getBoolean(PAYMENT_IN_P2P_FLAG);
        showResultDialog = getIntent()
                .getBooleanExtra(PAYMENT_IN_SHOW_RESULT_DIALOG, true);
    }

    private class P2pParams {
        private final String to;
        private final Double sum;
        private final String comment;
        private final String message;

        private P2pParams(String to, Double sum, String comment,
                String message) {
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

    private class RequestPaymentP2pTask extends
            AsyncTask<P2pParams, Void, RequestPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Подготовка перевода", Consts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResponse resp) {
            dialog.dismiss();
            String title = "Подготовка перевода";
            Intent resIntent = new Intent();
            if ((resp == null) || (error != null)) {
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                resIntent
                        .putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                setResult(Activity.RESULT_CANCELED, resIntent);
                if (showResultDialog) {
                    makeResultAlertDialog(false, error, null, title).show();
                } else {
                    finish();
                }
            } else {
                if (resp.isSuccess()) {
                    btnPayFromWallet.setOnClickListener(
                            new View.OnClickListener() {
                                public void onClick(View v) {
                                    ProcPayParam param = new ProcPayParam(
                                            resp.getRequestId(), MoneySource.wallet);
                                    new ProcessPaymentTask()
                                            .execute(param);
                                }
                            });
                } else {
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR,
                            resp.getError());
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                            resp.isSuccess());
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID,
                            resp.getRequestId());
                    setResult(Activity.RESULT_CANCELED, resIntent);
                    if (showResultDialog) {
                        makeResultAlertDialog(resp.isSuccess(), resp.getError(),
                                resp.getRequestId(), title);
                    } else {
                        finish();
                    }

                }
            }
        }

        @Override
        protected RequestPaymentResponse doInBackground(
                P2pParams... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                RequestPaymentResponse resp = ym.requestPaymentP2P(accessToken,
                        params[0].getTo(),
                        BigDecimal.valueOf(params[0].getSum()),
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

    private AlertDialog makeResultAlertDialog(final boolean isSuccess,
            final String error, final String operationId, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_wallet);
        if (title != null)
            builder.setTitle(title);
        if (isSuccess)
            builder.setMessage("Перевод успешно завершен");
        else
            builder.setMessage("Ошибка: " + error);

        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent authResult = new Intent();
                authResult.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                        isSuccess);
                authResult.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                authResult.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID,
                        operationId);

                if (isSuccess)
                    PaymentActivity.this
                            .setResult(Activity.RESULT_OK, authResult);
                else
                    PaymentActivity.this
                            .setResult(Activity.RESULT_CANCELED, authResult);
                finish();
            }
        });
        return builder.create();
    }

    private class RequestPaymentShopTask extends
            AsyncTask<PaymentShopParcelable, Void, RequestPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Подготовка перевода", Consts.WAIT);
            if (!PaymentActivity.this.isFinishing())
                dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResponse resp) {
            dialog.dismiss();
            String title = "Подготовка перевода";
            Intent resIntent = new Intent();
            if ((resp == null) || (error != null)) {
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                resIntent
                        .putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                setResult(Activity.RESULT_CANCELED, resIntent);
                if (showResultDialog) {
                    makeResultAlertDialog(false, error, null, title).show();
                } else {
                    finish();
                }
            } else {
                if (resp.isSuccess()) {
                    tvDescr.setText(resp.getContract());

                    if (resp.getMoneySource().getCard().getAllowed()) {
                        layoutPaywithCard.setVisibility(View.VISIBLE);
                        btnPayFromCard.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        ProcPayParam param = new ProcPayParam(
                                                resp.getRequestId(),
                                                MoneySource.card,
                                                edtCVC.getText().toString());
                                        new ProcessPaymentTask().execute(param);
                                    }
                                });
                    }

                    if (resp.getMoneySource().getWallet().getAllowed()) {
                        btnPayFromWallet.setVisibility(View.VISIBLE);
                        btnPayFromWallet
                                .setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        ProcPayParam param = new ProcPayParam(
                                                resp.getRequestId(),
                                                MoneySource.wallet);
                                        new ProcessPaymentTask().execute(param);
                                    }
                                });
                    }
                } else {
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR,
                            resp.getError());
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                            resp.isSuccess());
                    resIntent.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID,
                            resp.getRequestId());
                    setResult(Activity.RESULT_CANCELED, resIntent);
                    if (showResultDialog) {
                        makeResultAlertDialog(resp.isSuccess(), resp.getError(),
                                resp.getRequestId(), title);
                    } else {
                        finish();
                    }
                }
            }
        }

        @Override
        protected RequestPaymentResponse doInBackground(PaymentShopParcelable... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                RequestPaymentResponse resp = ym.requestPaymentShop(accessToken,
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

    private class ProcessPaymentTask extends
            AsyncTask<ProcPayParam, Void, ProcessPaymentResponse> {

        ProgressDialog dialog;
        String error;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Выполнение перевода", Consts.WAIT);
            dialog.show();
        }

        @Override
        protected void onPostExecute(
                ProcessPaymentResponse resp) {
            Intent resIntent = new Intent();
            dialog.dismiss();
            String title = "Перевод";
            if ((resp == null) || (error != null)) {
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                resIntent
                        .putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                setResult(Activity.RESULT_CANCELED, resIntent);
                if (showResultDialog) {
                    makeResultAlertDialog(false, error, null, title).show();
                } else {
                    finish();
                }
            } else {
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_ERROR,
                        resp.getError());
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS,
                        resp.isSuccess());
                resIntent.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID,
                        resp.getPaymentId());
                if (resp.isSuccess())
                    setResult(Activity.RESULT_OK, resIntent);
                else
                    setResult(Activity.RESULT_CANCELED, resIntent);
                if (showResultDialog) {
                    makeResultAlertDialog(resp.isSuccess(), resp.getError(),
                            resp.getPaymentId(), title).show();
                } else {
                    finish();
                }
            }
        }

        @Override
        protected ProcessPaymentResponse doInBackground(
                ProcPayParam... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                if (params[0].getMoneySource() == MoneySource.wallet)
                    return ym.processPaymentByWallet(accessToken,
                            params[0].getRequestId());
                if (params[0].getMoneySource() == MoneySource.card)
                    return ym.processPaymentByCard(accessToken,
                            params[0].getRequestId(), edtCVC.getText().toString());
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

    private class ProcPayParam {
        private final String requestId;
        private final MoneySource moneySource;
        private final String cvc;

        ProcPayParam(String requestId, MoneySource moneySource,
                String cvc) {
            this.requestId = requestId;
            this.moneySource = moneySource;
            this.cvc = cvc;
        }

        ProcPayParam(String requestId, MoneySource moneySource) {
            this.requestId = requestId;
            this.moneySource = moneySource;
            this.cvc = null;
        }

        public String getRequestId() {
            return requestId;
        }

        public MoneySource getMoneySource() {
            return moneySource;
        }

        public String getCvc() {
            return cvc;
        }
    }
}

