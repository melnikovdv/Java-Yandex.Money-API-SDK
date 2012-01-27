package ru.yandex.money.droid.activities2;

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
import ru.yandex.money.droid.R;
import ru.yandex.money.droid.preferences.LibConsts;
import ru.yandex.money.droid.utils.Utils;

import java.io.IOException;

/**
 * @author dvmelnikov
 */

public class AuthAct extends Activity {

    private ProgressDialog pd;

    private String clientId;
    private String redirectUri;

    private WebView authView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = Utils.makeProgressDialog(this, LibConsts.AUTH, LibConsts.WAIT);
        pd.show();

        setContentView(R.layout.ymd_auth);
        setupIntentParams();
        setupAuthView();

        authView.loadUrl(redirectUri);
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
        clientId = getIntent().getStringExtra(Consts2.AUTH_IN_CLIENT_ID);
        redirectUri =
                getIntent().getExtras().getString(Consts2.AUTH_IN_REDIRECT_URI);
    }

    private String extractCode(String urlWithCode) {
        Uri uri = Uri.parse(urlWithCode);
        return uri.getQueryParameter(LibConsts.CODE);
    }

    private ReceiveTokenResp receiveToken(String code) {
        YandexMoney ym = Utils.getYandexMoney(AuthAct.this);
        try {
            ReceiveOAuthTokenResponse resp =
                    ym.receiveOAuthToken(code,
                            Utils.getRedirectUri(AuthAct.this));
            if (resp.isSuccess()) {
                Utils.writeToken(AuthAct.this, clientId, resp.getAccessToken());
                return new ReceiveTokenResp(resp.getAccessToken(), null);
            } else {
                return new ReceiveTokenResp(resp.getAccessToken(),
                        resp.getError());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ReceiveTokenResp(null, e.getMessage());
        } catch (InsufficientScopeException e) {
            e.printStackTrace();
            return new ReceiveTokenResp(null, e.getMessage());
        }
    }

    private AlertDialog makeAlertDialog(final ReceiveTokenResp resp, final Intent intent) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_wallet);
        if (resp.isSuccess())
            builder.setMessage("Авторизация успешно завершена");
        else
            builder.setMessage("Ошибка: " + resp.getError());

        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (resp.isSuccess())
                    AuthAct.this.setResult(Activity.RESULT_OK, intent);
                else
                    AuthAct.this.setResult(Activity.RESULT_CANCELED, intent);
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
            if (url.contains(Utils.getRedirectUri(AuthAct.this))) {
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
            result.putExtra(Consts2.AUTH_OUT_IS_SUCCESS, response.isSuccess());
            result.putExtra(Consts2.AUTH_OUT_ACCESS_TOKEN, response.getToken());
            result.putExtra(Consts2.AUTH_OUT_ERROR, response.getError());

            boolean showResultDialog = getIntent()
                    .getBooleanExtra(Consts2.AUTH_IN_SHOW_RES_DLG, false);
            if (showResultDialog) {
                AlertDialog resDlg =
            }

            pd.dismiss();
        }

        @Override
        protected ReceiveTokenResp doInBackground(String... params) {
            return receiveToken(params[0]);
        }
    }

    private class ReceiveTokenResp {
        private String token;
        private String error;
        private boolean success;

        public ReceiveTokenResp(String token, String error) {
            success = token != null;
            this.error = error;
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
    }
}
