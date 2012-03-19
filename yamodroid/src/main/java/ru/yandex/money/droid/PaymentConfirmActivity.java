package ru.yandex.money.droid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author dvmelnikov
 */

public class PaymentConfirmActivity extends Activity {

    private String clientId;
    private String accessToken;
    private boolean showResultDialog;
    private PaymentShopParcelable shopParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(PaymentActivity.PAYMENT_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(PaymentActivity.PAYMENT_IN_ACCESS_TOKEN);
        showResultDialog = getIntent().getBooleanExtra(PaymentActivity.PAYMENT_IN_SHOW_RESULT_DIALOG, true);

        setContentView(R.layout.ymd_payment_confirm);
    }
}
