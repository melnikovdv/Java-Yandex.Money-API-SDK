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
    public static final String PAYMENT_IN_SHOW_RESULT_DIALOG = "ru.yandex.money.droid.show_result_dialog";

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
    private Button btnPay;
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
            btnPay = (Button) findViewById(R.id.btn_pay);

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
            btnPay = (Button) findViewById(R.id.btn_pay);
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
            AsyncTask<P2pParams, Void, RequestPaymentResp> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Подготовка перевода", Consts.WAIT);
            dialog.setOnCancelListener(new OnRequestCancel());
            if (!isFinishing())
                dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResp resp) {
            dialog.dismiss();

            if (resp.getException() == null) {
                if (resp.getResponse().isSuccess()) {
                    btnPay.setOnClickListener(
                            new View.OnClickListener() {
                                public void onClick(View v) {
                                    ProcPayParam param = new ProcPayParam(
                                            resp.getResponse().getRequestId(), MoneySource.wallet);
                                    new ProcessPaymentTask()
                                            .execute(param);
                                }
                            });
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                    intent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, resp.getResponse().getError());
                    PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                    PaymentActivity.this.finish();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.PAYMENT_OUT_EXCEPTION, resp.getException());
                PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                PaymentActivity.this.finish();
            }
        }

        @Override
        protected RequestPaymentResp doInBackground(
                P2pParams... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                RequestPaymentResponse resp = ym.requestPaymentP2P(accessToken,
                        params[0].getTo(),
                        BigDecimal.valueOf(params[0].getSum()),
                        params[0].getComment(), params[0].getMessage());
                return new RequestPaymentResp(resp, null);
            } catch (IOException e) {
                return new RequestPaymentResp(null, e);
            } catch (InvalidTokenException e) {
                return new RequestPaymentResp(null, e);
            } catch (InsufficientScopeException e) {
                return new RequestPaymentResp(null, e);
            }
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
                if (error != null)
                    authResult.putExtra(ActivityParams.PAYMENT_OUT_ERROR, error);
                if (operationId != null)
                    authResult.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID, operationId);

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
            AsyncTask<PaymentShopParcelable, Void, RequestPaymentResp> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Подготовка перевода", Consts.WAIT);
            dialog.setOnCancelListener(new OnRequestCancel());
            if (!isFinishing())
                dialog.show();
        }

        @Override
        protected void onPostExecute(
                final RequestPaymentResp resp) {
            dialog.dismiss();

            if (resp.getException() == null) {
                if (resp.getResponse().isSuccess()) {
                    tvDescr.setText(resp.getResponse().getContract());

                    if (resp.getResponse().getMoneySource().getCard().getAllowed()) {
                        layoutPaywithCard.setVisibility(View.VISIBLE);
                        btnPayFromCard.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ProcPayParam param = new ProcPayParam(
                                        resp.getResponse().getRequestId(),
                                        MoneySource.card,
                                        edtCVC.getText().toString());
                                new ProcessPaymentTask().execute(param);
                            }
                        });
                    } else
                        layoutPaywithCard.setVisibility(View.GONE);

                    if (resp.getResponse().getMoneySource().getWallet().getAllowed()) {
                        btnPay.setVisibility(View.VISIBLE);
                        btnPay.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ProcPayParam param = new ProcPayParam(
                                        resp.getResponse().getRequestId(),
                                        MoneySource.wallet);
                                new ProcessPaymentTask().execute(param);
                            }
                        });
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                    intent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, resp.getResponse().getError());
                    PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                    PaymentActivity.this.finish();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.PAYMENT_OUT_EXCEPTION, resp.getException());
                PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                PaymentActivity.this.finish();
            }
        }

        @Override
        protected RequestPaymentResp doInBackground(PaymentShopParcelable... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                RequestPaymentResponse resp = ym.requestPaymentShop(accessToken,
                        params[0].getPatternId(), params[0].getParams());
                return new RequestPaymentResp(resp, null);
            } catch (IOException e) {
                return new RequestPaymentResp(null, e);
            } catch (InvalidTokenException e) {
                return new RequestPaymentResp(null, e);
            } catch (InsufficientScopeException e) {
                return new RequestPaymentResp(null, e);
            }
        }
    }

    private class ProcessPaymentTask extends
            AsyncTask<ProcPayParam, Void, ProcessPaymentResp> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = Utils.makeProgressDialog(PaymentActivity.this,
                    "Выполнение перевода", Consts.WAIT);
            dialog.setCancelable(false);
            if (!isFinishing())
                dialog.show();
        }

        @Override
        protected void onPostExecute(ProcessPaymentResp resp) {
            dialog.dismiss();

            if (resp.getException() == null) {
                if (resp.getResponse().isSuccess()) {                    
                    if (showResultDialog) {
                        if (!isFinishing())
                            makeResultAlertDialog(true, null, resp.getResponse().getPaymentId(), "Перевод").show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, true);
                        intent.putExtra(ActivityParams.PAYMENT_OUT_OPERATION_ID, resp.getResponse().getPaymentId());
                        PaymentActivity.this.setResult(Activity.RESULT_OK, intent);
                        PaymentActivity.this.finish();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                    intent.putExtra(ActivityParams.PAYMENT_OUT_ERROR, resp.getResponse().getError());
                    PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                    PaymentActivity.this.finish();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
                intent.putExtra(ActivityParams.PAYMENT_OUT_EXCEPTION, resp.getException());
                PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                PaymentActivity.this.finish();
            }
        }

        @Override
        protected ProcessPaymentResp doInBackground(
                ProcPayParam... params) {
            try {
                YandexMoney ym = Utils.getYandexMoney(clientId);
                ProcessPaymentResponse resp = null;
                if (params[0].getMoneySource() == MoneySource.wallet)
                    resp = ym.processPaymentByWallet(accessToken, params[0].getRequestId());
                if (params[0].getMoneySource() == MoneySource.card)
                    resp = ym.processPaymentByCard(accessToken, params[0].getRequestId(),
                            edtCVC.getText().toString());
                return new ProcessPaymentResp(resp, null);
            } catch (IOException e) {
                return new ProcessPaymentResp(null, e);
            } catch (InsufficientScopeException e) {
                return new ProcessPaymentResp(null, e);
            } catch (InvalidTokenException e) {
                return new ProcessPaymentResp(null, e);
            }
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

    private class RequestPaymentResp {
        private RequestPaymentResponse response;
        private Exception exception;

        private RequestPaymentResp(RequestPaymentResponse response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        public RequestPaymentResponse getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }

    private class ProcessPaymentResp {
        private ProcessPaymentResponse response;
        private Exception exception;

        private ProcessPaymentResp(ProcessPaymentResponse response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        public ProcessPaymentResponse getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }
    
    private class OnRequestCancel implements DialogInterface.OnCancelListener {

        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);            
            PaymentActivity.this.setResult(Activity.RESULT_CANCELED, intent);
            PaymentActivity.this.finish();
        }
    }
}

