package com.gptwgl.swiftlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

public class FingerPrintLockScreen extends AppCompatActivity implements FahListener {

    private FingerprintAuthHelper mFAH;
    private ImageView lockHandle;
    private ImageView fpIcon;
    private CardView cardCircle;
    private TextView lockStatus;

    public static final String mphStr = "masterPasswordHash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#42c662"));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_lock_screen);

        mFAH = new FingerprintAuthHelper
                .Builder(this, this) //(Context inscance of Activity, FahListener)
                .build();

        lockHandle = (ImageView) findViewById(R.id.lockHandle);
        fpIcon = (ImageView) findViewById(R.id.fingerPrint);
        cardCircle = (CardView) findViewById(R.id.cardCircle);
        lockStatus = (TextView) findViewById(R.id.lockStatus);
    }

    public void applyRedThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.RED,
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    public void applyGreenThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.rgb(56,172,84),
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFAH.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mFAH.stopListening();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFAH.onDestroy();
    }

    @Override
    public void onFingerprintStatus(boolean authSuccessful, int errorType, CharSequence errorMess) {
        // authSuccessful - boolean that shows auth status
        // errorType - if auth was failed, you can catch error type
        // errorMess - if auth was failed, errorMess will tell you (and user) the reason

        if (authSuccessful){
            // do some stuff here in case auth was successful
//            Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();

            CircularProgressBar circularProgressBar = (CircularProgressBar)findViewById(R.id.progressbar);
            int animationDuration = 1200; // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(100, animationDuration); // Default duration = 1500ms

            ViewAnimator
                    .animate(lockHandle)
                    .duration(0)
                    .dp().translationY(0, -20)
                    .start();

            applyGreenThemeToDrawable(fpIcon.getDrawable());

            lockStatus.setText("Success! Decrypting data..");

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

        } else if (mFAH != null){
            // do some stuff here in case auth failed
            switch (errorType){
                case FahErrorType.General.LOCK_SCREEN_DISABLED:
                case FahErrorType.General.NO_FINGERPRINTS:
                    mFAH.showSecuritySettingsDialog();
                    break;
                case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:
                    //do some stuff here
                    applyRedThemeToDrawable(fpIcon.getDrawable());

                    ViewAnimator
                            .animate(cardCircle)
                            .shake().interpolator(new LinearInterpolator())
                            .duration(600)
                            .start();

                    lockStatus.setText("Fingerprint not recognized. Try again!");

                    break;
                case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                    //do some stuff here
                    break;
            }
        }
    }

    @Override
    public void onFingerprintListening(boolean listening, long milliseconds) {
        // listening - status of fingerprint listen process
        // milliseconds - timeout value, will be > 0, if listening = false & errorType = AUTH_TO_MANY_TRIES

        if (listening){
            //add some code here
        } else {
            //add some code here
        }
        if (milliseconds > 0) {
            //if u need, u can show timeout for user
            lockStatus.setText("Too many attempts! Please wait for " + (milliseconds / 1000) + " seconds");
        } else if(milliseconds == 0) {
            lockStatus.setText("Scan your finger to unlock");
        }
    }
}
