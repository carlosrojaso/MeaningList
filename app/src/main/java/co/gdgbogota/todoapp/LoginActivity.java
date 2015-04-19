package co.gdgbogota.todoapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;


public class LoginActivity extends ActionBarActivity {

    private Button facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookButton = (Button) findViewById(R.id.facebook_button);
    }

}
