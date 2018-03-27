package com.gptwgl.swiftlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetMasterPW extends AppCompatActivity {

    private EditText masterPassword1;
    private EditText masterPassword2;
    private TextView errorView;
    private Button confirmBtn;

    SharedPreferences sharedpreferences;
    public static final String mpHashCode = "masterPasswordHash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_master_pw);

        masterPassword1 = (EditText) findViewById(R.id.masterPassword1);
        masterPassword2 = (EditText) findViewById(R.id.masterPassword2);
        errorView = (TextView) findViewById(R.id.errorTextView);
        confirmBtn = (Button) findViewById(R.id.setMasterPasswordBtn);

        sharedpreferences = getSharedPreferences("swiftLoginPrefs",
                Context.MODE_PRIVATE);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( masterPassword1.getText().toString().equals( masterPassword2.getText().toString() ) ){

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(mpHashCode, SwiftLogin.md5(masterPassword1.getText().toString()));
                    editor.apply();

                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    errorView.setText("Passwords Don't Match! Verify Them.");
                }
            }
        });
    }
}
