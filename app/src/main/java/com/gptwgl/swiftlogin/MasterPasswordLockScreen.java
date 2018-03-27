package com.gptwgl.swiftlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MasterPasswordLockScreen extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String mpHashCode = "masterPasswordHash";
    private EditText masterPassword;
    private ImageView lockHandle;
    private TextView lockStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#42c662"));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password_lock_screen);

        sharedpreferences = getSharedPreferences("swiftLoginPrefs",
                Context.MODE_PRIVATE);

        // Get Views.
        lockHandle = (ImageView) findViewById(R.id.mpLockHandle);
        lockStatus = (TextView) findViewById(R.id.mpLockStatus);

        // Authentication.
        masterPassword = (EditText) findViewById(R.id.masterPassword);

        masterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String hash = SwiftLogin.md5(editable.toString());

//                Toast.makeText(getBaseContext(), hash, Toast.LENGTH_SHORT).show();
                if( sharedpreferences.getString(mpHashCode, "").equals(hash) ) {

                    lockStatus.setText("Success! Decrypting data..");

                    ViewAnimator
                            .animate(lockHandle)
                            .duration(0)
                            .dp().translationY(0, -20)
                            .start();

                    CircularProgressBar circularProgressBar = (CircularProgressBar)findViewById(R.id.mpProgressBar);
                    int animationDuration = 1200;
                    circularProgressBar.setProgressWithAnimation(100, animationDuration);

                    new CountDownTimer(1200,1000) {

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }.start();


                }
            }
        });


    }
}
