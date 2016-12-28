package com.hackillinois.hackillinoiscluehunt.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


public class login_activity extends AppCompatActivity {

    //Github OAuth parts
    private static final String AUTHORIZATION_URL = "https://github.com/login/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String SCOPE = "user:email";
    private static final String CLIENT_ID = "36d4df97575bd10e4a38";
    private static final String CLIENT_SECRET = "87f7333f6053a558230296def5d84955db1f37d0";
    private static final String STATE = "78nyk7c9";
    private static final String REDIRECT_URI = "https://auth.firebase.com/v2/hi-scavenger2016/auth/github/callback";

    //Used to contruct the URLs
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String CODE_PARAM = "code";
    private static final String STATE_PARAM = "state";
    private static final String SCOPE_PARAM= "scope";

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private String token, userName, picUrl, email;
    private ProgressDialog pd;
    private SharedPreferences.Editor editor;

    @Bind(R.id.web_view) WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.github_login);
        ButterKnife.bind(this);

        SharedPreferences prefs = getSharedPreferences(clueHuntActivity.PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.welcome_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl){
                if(authorizationUrl.startsWith(REDIRECT_URI)){
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if(stateToken==null || !stateToken.equals(STATE)){
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    String authorizationToken = uri.getQueryParameter(CODE_PARAM);
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    new PostRequestAsyncTask().execute(accessTokenUrl);
                }else{
                    Log.i("Authorize","Redirecting to: "+authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        pd = ProgressDialog.show(this, "", "Logging in..",true);
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        //Load the authorization URL into the webView
        webView.loadUrl(authUrl);
    }

    //Used for GET request
    private static String getAuthorizationUrl(){
        String URL = AUTHORIZATION_URL
                        +QUESTION_MARK
                        +CLIENT_ID_PARAM+EQUALS+CLIENT_ID
                        +AMPERSAND
                        +SCOPE_PARAM + EQUALS + SCOPE
                        +AMPERSAND
                        +STATE_PARAM + EQUALS + STATE;
        Log.i("authorization URL", "" + URL);
        return URL;
    }

    //Used for POST request
    private static String getAccessTokenUrl(String authorizationToken){
        String URL = ACCESS_TOKEN_URL
                        +QUESTION_MARK
                        +CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                        +AMPERSAND
                        +CLIENT_SECRET_PARAM + EQUALS + CLIENT_SECRET
                        +AMPERSAND
                        +CODE_PARAM + EQUALS + authorizationToken
                        +AMPERSAND
                        +STATE_PARAM + EQUALS + STATE;
        Log.i("accessToken URL", "" + URL);
        return URL;
    }

    //Firebase Github Login
    private void login(){
        Firebase ref = new Firebase("https://hi-scavenger2016.firebaseio.com/");
        ref.authWithOAuthToken("github", token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                editor.putBoolean("loggedIn", true).commit();
                nextActivity();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                makeToast(firebaseError.getMessage());
            }
        });
    }

    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void nextActivity(){
        Intent intent = new Intent(getApplicationContext(), clueHuntActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
        this.finish();
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params){
            if(params.length > 0) {
                try{
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    StringBuilder response = new StringBuilder();
                    InputStream in = conn.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        response.append(current);
                    }
                    isw.close();

                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200) {
                        String accessToken = response.toString();
                        token = accessToken.substring(accessToken.indexOf('=')+1, accessToken.indexOf('&'));
                        Log.e("Token", "" + accessToken);
                        editor.putString("accessToken", token).apply();
                        return true;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if(status){
                new InfoRequestTask().execute("https://api.github.com/user?access_token=" + getSharedPreferences(clueHuntActivity.PREFS_NAME, MODE_PRIVATE).getString("accessToken", ""));
                login(); //Login if correct access token was retrieved
            }
        }
    }

    //Retrieves user's Github username and avatar url
    private class InfoRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            userName = "";
            picUrl = "";
            email = "";

            if(params.length > 0){
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();
                    Log.e("URL", params[0]);
                    StringBuilder response = new StringBuilder();
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String node;
                    while((node = reader.readLine()) != null){
                        response.append(node);
                    }
                    reader.close();

                    int responseCode = con.getResponseCode();
                    if(responseCode == 200){
                        JSONObject userDets = new JSONObject(response.toString());
                        userName = userDets.getString("login");
                        picUrl = userDets.getString("avatar_url");
                        email = userDets.getString("email");
                        Log.e("Username", userName);
                        editor.putString("userName", userName);
                        editor.putString("picUrl", picUrl);
                        editor.putString("userEmail", email);
                        editor.commit();
                        setupFirstTimeUser();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return userName;
        }
    }

    //Sets up user for the first time
    private void setupFirstTimeUser(){
        Firebase ref = new Firebase("https://hi-scavenger2016.firebaseio.com/users").child(userName).child("profile");
        ref.child("username").setValue(userName);
        ref.child("avatarUrl").setValue(picUrl);
        ref.child("email").setValue(email);
        ref.child("score").setValue(0);
        ref.child("finished_hunt").setValue(false);
    }

}
