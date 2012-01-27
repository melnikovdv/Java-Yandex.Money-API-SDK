package ru.yandex.money.droid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

public class AuthActivity extends Activity {

    private Context context;
    private WebView webView;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog =
                Utils.makeProgressDialog(this, LibConsts.AUTH, LibConsts.WAIT);
        progressDialog.show();

        clientId = getIntent().getStringExtra(LibConsts.PREF_CLIENT_ID);
        String sUri = getIntent().getExtras().getString(LibConsts.URI);

        setContentView(R.layout.ymd_auth);
        context = this;

        webView = (WebView) findViewById(R.id.wv_auth);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.setWebChromeClient(new AuthWebChromeClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings()
                .setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);



        webView.loadUrl(sUri);

    }

    private class AuthWebViewClient extends WebViewClient {

        private AuthWebViewClient() {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(Utils.getRedirectUri(context))) {
                view.goBack();
                new StartAuthResultActivity().execute(extractCode(url));
                return false;
            }

            webView.loadUrl(url);
            return true;
        }
    }

    private class AuthWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100)
                progressDialog.dismiss();
        }
    }

    private String extractCode(String urlWithCode) {
        Uri uri = Uri.parse(urlWithCode);
        return uri.getQueryParameter(LibConsts.CODE);
    }

    private class StartAuthResultActivity extends
            AsyncTask<String, Void, ReceiveTokenResponse> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(
                ReceiveTokenResponse receiveTokenResponse) {
            dialog = Utils.makeResultAlertDialog(AuthActivity.this,
                    "Авторизация",
                    "Успешно завершена", receiveTokenResponse.getError());
            progressDialog.dismiss();
            dialog.show();
        }

        @Override
        protected ReceiveTokenResponse doInBackground(String... params) {
            return receiveToken(params[0]);
        }
    }

    private ReceiveTokenResponse receiveToken(String code) {
        YandexMoney ym = Utils.getYandexMoney(context);
        try {
            ReceiveOAuthTokenResponse resp =
                    ym.receiveOAuthToken(code, Utils.getRedirectUri(context));
            if (resp.isSuccess()) {
                Utils.writeToken(context, clientId, resp.getAccessToken());
                return new ReceiveTokenResponse(resp.getAccessToken(), null);
            } else {
                return new ReceiveTokenResponse(resp.getAccessToken(),
                        resp.getError());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ReceiveTokenResponse(null, e.getMessage());            
        } catch (InsufficientScopeException e) {
            e.printStackTrace();
            return new ReceiveTokenResponse(null, e.getMessage());
        }
    }

    private class ReceiveTokenResponse {
        private String token;
        private String error;
        private boolean success;

        public ReceiveTokenResponse(String token, String error) {
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
