package com.gptwgl.swiftlogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;

import com.google.gson.Gson;

import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

class Session {
    String tabUrl;
    List<Map<String, String>> cookies = new ArrayList();
}

public class AccountLogin extends XWalkActivity {
    private static final String TAG = "SL_TEST";
    private XWalkView mXWalkView;
    private SwiftLogin swiftLogin;
    private int accountId;
    private Context mContext;
    private XWalkCookieManager xWCM = new XWalkCookieManager();

    private Map<String, String> account;
    private String swiftObjJSON;
    private String slFiller;
    private Button saveSessBtn;

    private Session session = new Session();

    private int loadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        accountId = getIntent().getExtras().getInt("account_id");
        swiftLogin = new SwiftLogin(getBaseContext());


        saveSessBtn = (Button) findViewById(R.id.save_session);
        saveSessBtn.setVisibility(View.GONE);


        mXWalkView = (XWalkView) findViewById(R.id.xwalkWebView);

    }

    @Override
    public void onXWalkReady() {

        account = swiftLogin.getAccount(accountId);
        final String loginUrl = account.get("site_login_url");
        final String siteUrl = account.get("site_url");

        final Map<String, String> swiftObj = new HashMap<>();

        swiftObj.put("email", account.get("email"));
        swiftObj.put("username", account.get("username"));
        swiftObj.put("password", account.get("password"));

        swiftObjJSON = (new Gson()).toJson(swiftObj);
        slFiller = getFillerScript();

        session.tabUrl = siteUrl;

        mXWalkView.setResourceClient(new XWalkResourceClient(mXWalkView) {

            @Override
            public void onReceivedResponseHeaders(XWalkView view,
                                                  XWalkWebResourceRequest request,
                                                  XWalkWebResourceResponse response) {

                super.onReceivedResponseHeaders(view, request, response);

                Map<String, String> requestHeaders = request.getRequestHeaders();
                Map<String, String> responseHeaders = response.getResponseHeaders();

                String cookies = responseHeaders.get("set-cookie");

                if( cookies == null ){
                    cookies = responseHeaders.get("Set-Cookie");
                }

                if( cookies != null ){

                    String host = requestHeaders.get("Host");

                    if( host == null ){
                        host = requestHeaders.get("host");
                    }

                    addSessionItems(host, request.getUrl().toString(), cookies);
                }

            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);

                loadCount++;

                if(loadCount == 2){
                    saveSessBtn.setVisibility(View.VISIBLE);
                }

                String swiftObjJs = "(function(){ window.swiftObj="+ swiftObjJSON +"; })();";

                view.evaluateJavascript( swiftObjJs + slFiller, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });

        String s1 = mXWalkView.getUserAgentString();
        String s2 = mXWalkView.getUserAgentString().replace("Crosswalk", "");

        mXWalkView.setUserAgentString("Mozilla/5.0 (Linux; Android 6.0; XT1706 Build/MRA58K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91 Mobile Safari/537.36");

        mXWalkView.load(loginUrl, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXWalkView != null) {
            mXWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXWalkView != null) {
            mXWalkView.onNewIntent(intent);
        }
    }

    public void onClickSaveSession(View v){

        Gson gson = new Gson();
        String gsonSession = gson.toJson(session);

        swiftLogin.updateSession(accountId, gsonSession);
        setResult(16);
        finish();

        for (int i =0; i < 2; i++){
            xWCM.removeAllCookie();
            xWCM.flushCookieStore();
        }

        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void addSessionItems( String host, String url, String cookieStr ){


        try {

            HttpUrl mUrl = HttpUrl.parse(url);

            // Replace "," with "##" in Expire attribute of cookie.
            cookieStr = cookieStr.replaceAll("(Expires=.{3}|expires=.{3,10})\\,", "$1##");

            String[] cookies = cookieStr.split(",");

            for (int i=0; i < cookies.length; i++ ){
                Map<String, String> sessionItem = new HashMap();

                cookies[i] = cookies[i].replace("##", ",").trim();
                Cookie2 cookieObj = Cookie2.parse(mUrl, cookies[i]);

                if( cookieObj.persistent() ){

                    try{

                        sessionItem.put("host", host);
                        sessionItem.put("url", url);
                        sessionItem.put("name", cookieObj.name());
                        sessionItem.put("value", cookieObj.value());
                        sessionItem.put("path", cookieObj.path());
                        sessionItem.put("expirationDate", String.valueOf(cookieObj.expiresAt()));

                        if( ! cookieObj.hostOnly() ) {
                            sessionItem.put("domain", cookieObj.domain());
                        }

                        if( cookieObj.secure() ){
                            sessionItem.put("secure", "true");
                        }

                        if( cookieObj.httpOnly() ){
                            sessionItem.put("httpOnly", "true");
                        }

                    }catch (NullPointerException e){
                        Log.e("AccountLogin", "Nullpointer Exception: " + e.getMessage());
                    }

                    session.cookies.add(sessionItem);
                }

            }

        } catch (NullPointerException e){
            Log.e("AccountLogin", "Nullpointer Exception: " + e.getMessage());
        }

    }

    private String getFillerScript(){
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = getResources().openRawResource(R.raw.sl_filler);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }
}
