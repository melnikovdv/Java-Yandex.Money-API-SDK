package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import ru.yandex.money.api.enums.MoneySource;

/**
 * @author dvmelnikov
 */

public class PaymentConfirmActivity extends Activity {
        
    public static final String PAYMENT_CONFIRM_IN_REQUEST_ID = "ru.yandex.money.droid.request_id";

    private String clientId;
    private String accessToken;
    private boolean showResultDialog;    
    private String operationId;    
    
    private RadioButton rBtnWallet;
    private RadioButton rBtnCard;
    private EditText edtCVC;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = getIntent().getStringExtra(PaymentActivity.PAYMENT_IN_CLIENT_ID);
        accessToken = getIntent().getStringExtra(PaymentActivity.PAYMENT_IN_ACCESS_TOKEN);
        showResultDialog = getIntent().getBooleanExtra(PaymentActivity.PAYMENT_IN_SHOW_RESULT_DIALOG, true);

        operationId = getIntent().getStringExtra(PAYMENT_CONFIRM_IN_REQUEST_ID);        

        setContentView(R.layout.ymd_payment_confirm);

        rBtnWallet = (RadioButton) findViewById(R.id.radio_btn_wallet);
        rBtnCard = (RadioButton) findViewById(R.id.radio_btn_card);
        edtCVC = (EditText) findViewById(R.id.cvc_edit);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);        
        
        rBtnWallet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rBtnWallet.setChecked(true);
                rBtnCard.setChecked(false);
                edtCVC.setVisibility(View.GONE);
            }
        });
        
        rBtnCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rBtnWallet.setChecked(false);
                rBtnCard.setChecked(true);
                edtCVC.setVisibility(View.VISIBLE);
            }
        });
        
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MoneySource msource = MoneySource.wallet;
                if (rBtnCard.isChecked()) {
                    msource = MoneySource.card;                
                }
                                
                new ProcessPaymentTask(PaymentConfirmActivity.this, clientId, accessToken, operationId, msource, 
                        showResultDialog, edtCVC.getText().toString()).execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityParams.PAYMENT_OUT_IS_SUCCESS, false);
        this.setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
