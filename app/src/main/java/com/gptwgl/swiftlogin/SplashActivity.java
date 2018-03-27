package com.gptwgl.swiftlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static android.R.id.list;

public class SplashActivity extends AppCompatActivity implements FahListener {

    private FingerprintAuthHelper mFAH;
    SharedPreferences sharedpreferences;
    public static final String mpHashCode = "masterPasswordHash";
    private SwiftLogin swiftLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#42c662"));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        swiftLogin = new SwiftLogin(this);

        sharedpreferences = getSharedPreferences("swiftLoginPrefs",
                Context.MODE_PRIVATE);

        mFAH = new FingerprintAuthHelper
                .Builder(this, this) //(Context inscance of Activity, FahListener)
                .build();

        /** Creates a count down timer, which will be expired after 5000 milliseconds */
        new CountDownTimer(1000,1000) {

            /** This method will be invoked on finishing or expiring the timer */
            @Override
            public void onFinish() {

                // Check for fingerprint hardware
                if (mFAH.isHardwareEnable()){
                    // Fingerprint scanner available
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("fingerPrintAvailable", "true");
                    editor.apply();

                    Intent intent;
                    if(swiftLogin.getAccounts().size() == 0){
                        intent = new Intent(getBaseContext(), HomeActivity.class);
                    } else {
                        intent = new Intent(getBaseContext(), FingerPrintLockScreen.class);
                    }

                    startActivity(intent);
                    finish();

                } else {
                    //not available
                    if (sharedpreferences.contains(mpHashCode)) {

//                        Toast.makeText(getBaseContext(),sharedpreferences.getString(mpHashCode, ""), Toast.LENGTH_LONG ).show();

                        Intent intent = new Intent(getBaseContext(), MasterPasswordLockScreen.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Intent intent = new Intent(getBaseContext(), SetMasterPW.class);
                        startActivity(intent);
                        finish();
                    }
                }

//                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            }

            /** This method will be invoked in every 1000 milli seconds until
             * this timer is expired.Because we specified 1000 as tick time
             * while creating this CountDownTimer
             */
            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }

    @Override
    public void onFingerprintStatus(boolean b, int i, @Nullable CharSequence charSequence) {

    }

    @Override
    public void onFingerprintListening(boolean b, long l) {

    }
}
