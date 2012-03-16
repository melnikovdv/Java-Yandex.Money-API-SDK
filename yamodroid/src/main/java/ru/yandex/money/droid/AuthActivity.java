package ru.yandex.money.droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;

import java.io.IOException;

/**
 * @author dvmelnikov
 */

public class AuthActivity extends Activity {

    private ProgressDialog pd;

    private String clientId;
    private String redirectUri;
    private String authUri;

    private WebView authView;

    public static final String AUTH_IN_SHOW_RES_DLG = "show_result_dialog";
    public static final String AUTH_IN_REDIRECT_URI = "redirect_uri";
    public static final String AUTH_IN_CLIENT_ID = "client_id";
    public static final String AUTH_IN_AUTH_URI = "authorize_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = Utils.makeProgressDialog(this, Consts.AUTH, Consts.WAIT);
        pd.show();

        setContentView(R.layout.ymd_auth);
        setupIntentParams();
        setupAuthView();

        authView.loadUrl(authUri);
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

    private void setupAuthView() {
        authView = (WebView) findViewById(R.id.wv_auth);
        authView.setWebViewClient(new AuthWebViewClient());
        authView.setWebChromeClient(new AuthWebChromeClient());
        authView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        authView.getSettings()
                .setRenderPriority(WebSettings.RenderPriority.HIGH);
        authView.getSettings().setJavaScriptEnabled(false);
        authView.getSettings().setBuiltInZoomControls(true);
    }

    private void setupIntentParams() {
        clientId = getIntent().getStringExtra(AuthActivity.AUTH_IN_CLIENT_ID);
        redirectUri =
                getIntent().getStringExtra(AuthActivity.AUTH_IN_REDIRECT_URI);
        authUri = getIntent().getStringExtra(AuthActivity.AUTH_IN_AUTH_URI);
    }

    private String extractCode(String urlWithCode) {
        Uri uri = Uri.parse(urlWithCode);
        return uri.getQueryParameter(Consts.AUTH_CODE);
    }

    private ReceiveTokenResp receiveToken(String code) {
        YandexMoney ym = Utils.getYandexMoney(clientId);
        try {
            ReceiveOAuthTokenResponse resp =
                    ym.receiveOAuthToken(code,
                            redirectUri);
            if (resp.isSuccess()) {
                return new ReceiveTokenResp(resp.getAccessToken(), null, null);
            } else {
                return new ReceiveTokenResp(resp.getAccessToken(),
                        resp.getError(), null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ReceiveTokenResp(null, null, e);
        } catch (InsufficientScopeException e) {
            e.printStackTrace();
            return new ReceiveTokenResp(null, e.getMessage(), e);
        }
    }

    private AlertDialog makeAlertDialog(final ReceiveTokenResp resp) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_wallet);
        builder.setTitle("Авторизация");
        if (resp.isSuccess())
            builder.setMessage("успешно завершена");
        else
            builder.setMessage("Ошибка: " + resp.getError());

        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent authResult = new Intent();
                authResult.putExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN, resp.getToken());
                authResult.putExtra(ActivityParams.AUTH_OUT_ERROR, resp.getError());
                authResult.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, resp.isSuccess());
                authResult.putExtra(ActivityParams.AUTH_OUT_EXCEPTION, resp.getException());
                
                if (resp.isSuccess())
                    AuthActivity.this.setResult(Activity.RESULT_OK, authResult);
                else
                    AuthActivity.this.setResult(Activity.RESULT_CANCELED, authResult);
                finish();
            }
        });
        return builder.create();
    }

    private class AuthWebViewClient extends WebViewClient {

        private AuthWebViewClient() {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(redirectUri)) {
                view.goBack();
                new StartAuthResultActivity().execute(extractCode(url));
                return false;
            }

            authView.loadUrl(url);
            return true;
        }
    }

    private class AuthWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100)
                pd.dismiss();
        }
    }

    private class StartAuthResultActivity extends
            AsyncTask<String, Void, ReceiveTokenResp> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected void onPostExecute(ReceiveTokenResp response) {
            Intent result = new Intent();
            result.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, response.isSuccess());
            result.putExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN, response.getToken());
            result.putExtra(ActivityParams.AUTH_OUT_ERROR, response.getError());
            result.putExtra(ActivityParams.AUTH_OUT_EXCEPTION, response.getException());            

            boolean showResultDialog = getIntent()
                    .getBooleanExtra(AuthActivity.AUTH_IN_SHOW_RES_DLG, false);
            pd.dismiss();
            if (showResultDialog) {
                AlertDialog resDlg = makeAlertDialog(response);
                resDlg.show();
            } else {
                if (response.isSuccess())
                    setResult(Activity.RESULT_OK, result);                
                else
                    setResult(Activity.RESULT_CANCELED, result);
                finish();
            }
        }

        @Override
        protected ReceiveTokenResp doInBackground(String... params) {
            return receiveToken(params[0]);
        }
    }

    private class ReceiveTokenResp {
        private final String token;
        private final String error;
        private final boolean success;
        private final Exception exception;

        public ReceiveTokenResp(String token, String error, Exception exception) {
            success = token != null;
            this.token = token;
            this.error = error;
            this.exception = exception; 
        }

        public String getToken() {
            return token;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return success;
        }

        public Exception getException() {
            return exception;
        }
    }
}
