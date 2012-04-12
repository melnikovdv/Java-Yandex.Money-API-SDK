package ru.yandex.money.droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
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
    private String clientSecret;

    private WebView authView;

    public static final String AUTH_IN_SHOW_RES_DLG = "show_result_dialog";
    public static final String AUTH_IN_REDIRECT_URI = "redirect_uri";
    public static final String AUTH_IN_CLIENT_ID = "client_id";
    public static final String AUTH_IN_AUTH_URI = "authorize_uri";
    public static final String AUTH_IN_SECRET = "client_secret";

    public static String AUTH_CODE = "code";
    public static final String AUTH = "Авторизация";    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = Utils.makeProgressDialog(this, AUTH, Consts.WAIT);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Intent result = new Intent();
                result.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);
                setResult(Activity.RESULT_CANCELED, result);
                finish();
            }
        });
        if (!isFinishing())
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
        redirectUri = getIntent().getStringExtra(AuthActivity.AUTH_IN_REDIRECT_URI);
        clientSecret = getIntent().getStringExtra(AuthActivity.AUTH_IN_SECRET);        
        authUri = getIntent().getStringExtra(AuthActivity.AUTH_IN_AUTH_URI);
    }

    private String extractCode(String urlWithCode) {
        Uri uri = Uri.parse(urlWithCode);
        return uri.getQueryParameter(AUTH_CODE);
    }

    private ReceiveTokenResp receiveToken(String code) {
        AndroidHttpClient client = Utils.httpClient();
        YandexMoney ym = Utils.getYandexMoney(clientId, client);
        try {
            ReceiveOAuthTokenResponse resp;
            if (clientSecret == null)
                resp = ym.receiveOAuthToken(code, redirectUri);
            else
                resp = ym.receiveOAuthToken(code, redirectUri, clientSecret);
            return new ReceiveTokenResp(resp, null);            
        } catch (IOException e) {
            return new ReceiveTokenResp(null, e);
        } catch (InsufficientScopeException e) {
            return new ReceiveTokenResp(null, e);
        } finally {
            client.close();
        }
    }

    private AlertDialog makeAlertDialog(final ReceiveTokenResp resp) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_wallet);
        builder.setTitle("Авторизация");
        if (resp.getResponse().isSuccess())
            builder.setMessage("успешно завершена");
        else
            builder.setMessage("Ошибка: " + resp.getResponse().getError());

        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent authResult = new Intent();
                if (resp.getResponse().getAccessToken() != null)
                    authResult.putExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN, resp.getResponse().getAccessToken());
                if (resp.getResponse().getError() != null)
                    authResult.putExtra(ActivityParams.AUTH_OUT_ERROR, resp.getResponse().getError());                
                authResult.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, resp.getResponse().isSuccess());
                if (resp.getException() != null)
                    authResult.putExtra(ActivityParams.AUTH_OUT_EXCEPTION, resp.getException());
                
                if (resp.getResponse().isSuccess())
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
            if (!isFinishing())
                pd.show();
        }

        @Override
        protected void onPostExecute(ReceiveTokenResp resp) {
            pd.dismiss();
            if (resp.getException() == null) {
                if (resp.getResponse().isSuccess()) {
                    boolean showResultDialog = getIntent().getBooleanExtra(AuthActivity.AUTH_IN_SHOW_RES_DLG, false);
                    if (showResultDialog) {
                        if (!isFinishing())
                            makeAlertDialog(resp).show();    
                    } else {
                        Intent result = new Intent();
                        result.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, resp.getResponse().isSuccess());
                        result.putExtra(ActivityParams.AUTH_OUT_ACCESS_TOKEN, resp.getResponse().getAccessToken());
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    }                    
                } else {
                    Intent result = new Intent();
                    result.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);
                    result.putExtra(ActivityParams.AUTH_OUT_ERROR, resp.getResponse().getError());
                    setResult(Activity.RESULT_CANCELED, result);
                    finish();
                }
            } else {
                Intent result = new Intent();
                result.putExtra(ActivityParams.AUTH_OUT_IS_SUCCESS, false);                
                result.putExtra(ActivityParams.AUTH_OUT_EXCEPTION, resp.getException());
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
        ReceiveOAuthTokenResponse response;
        private final Exception exception;

        public ReceiveTokenResp(ReceiveOAuthTokenResponse response, Exception exception) {
            this.response = response;
            this.exception = exception; 
        }

        public ReceiveOAuthTokenResponse getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }
    }
}
