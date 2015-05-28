package com.carlosrojasblog.meaninglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;



public class LoginActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.carlosrojasblog.meaninglist.MESSAGE";

    private LoginButton facebookButton;
    private CallbackManager callbackManager;
    private AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);

            facebookButton = (LoginButton) findViewById(R.id.facebook_button);

            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();


            facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    //Toast.makeText(getApplicationContext(), loginResult.getAccessToken().getUserId(), Toast.LENGTH_LONG).show();
                    //setContentView(R.layout.activity_main);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, loginResult.getAccessToken().getUserId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancel() {
                    Log.v("LoginActivity", "cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Something has gone wrong. Check your internet connection and try again.", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.v("LoginActivity", exception.toString());

                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);


    }

}
