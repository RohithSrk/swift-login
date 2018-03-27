package com.gptwgl.swiftlogin;

import android.app.Activity;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;
import android.os.Handler;

public class MyCaptureManager extends CaptureManager {
    private Activity activity;
    private DecoratedBarcodeView barcodeView;
    private SwiftLogin swiftLogin;
    private BeepManager beepManager;

    MyCaptureManager(Activity activity, DecoratedBarcodeView barcodeView){
        super(activity, barcodeView);
        this.activity = activity;
        this.barcodeView = barcodeView;
        swiftLogin = new SwiftLogin(activity.getBaseContext());
        beepManager = new BeepManager(activity);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            barcodeView.pause();
            beepManager.playBeepSoundAndVibrate();

            barcodeView.pause();
            String device_fid = result.getResult().getText();

            // Check device_fid using regex
            if(true){

                Toast.makeText(activity.getBaseContext(), "Got it : " + device_fid, Toast.LENGTH_LONG).show();


                (new Handler()).post(new Runnable() {
                    @Override
                    public void run() {
                        returnResult(result);
                    }
                });
            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    public void decode() {
        barcodeView.decodeSingle(callback);
    }


}
