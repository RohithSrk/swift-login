package com.gptwgl.swiftlogin;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class EditAccount extends AppCompatActivity {

    private Context mContext = this;
    private boolean isNewAccount;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        final SwiftLogin swiftLogin = new SwiftLogin(mContext);

        isNewAccount =  getIntent().getBooleanExtra("new_account", true);

        // Widgets and Text Fields.
        final ImageView siteLogo = (ImageView) findViewById(R.id.site_logo);
        final Button saveBtn = (Button) findViewById(R.id.save_button);
        final TextView siteName = (TextView) findViewById(R.id.site_name);
        final TextView siteURL = (TextView) findViewById(R.id.site_url);
        final TextView siteLoginURL = (TextView) findViewById(R.id.site_login_url);
        final TextView userName = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final TextView email = (TextView) findViewById(R.id.email);


        // TODO: Get Favicon if drawable is unavailable.
        String drawable = null;

        if( isNewAccount ) {
            String template_id =  getIntent().getStringExtra("template_id");

            if( ! template_id.equals( "other" ) ){
                final Map<String, String> template = swiftLogin.getTemplate(Integer.parseInt(template_id));
                drawable = template.get("logo");
                int resId = mContext.getResources().getIdentifier(drawable, "drawable", mContext.getPackageName());
                siteLogo.setImageResource(resId);
                siteName.setText(template.get("site_name"));
                siteURL.setText(template.get("site_url"));
                siteLoginURL.setText(template.get("site_login_url"));
                userName.requestFocus();
            } else {
                drawable = "other";
                siteLogo.setImageResource(R.drawable.other);
            }

        } else {
            accountId =  getIntent().getExtras().getInt("account_id");
            final Map<String, String> account = swiftLogin.getAccount(accountId);
            drawable = account.get("logo");
            int resId = mContext.getResources().getIdentifier(drawable, "drawable", mContext.getPackageName());
            siteLogo.setImageResource(resId);
            userName.setText(account.get("username"));
            password.setText(account.get("password"));
            email.setText(account.get("email"));
            siteName.setText(account.get("site_name"));
            siteURL.setText(account.get("site_url"));
            siteLoginURL.setText(account.get("site_login_url"));
        }

        final String logo = drawable;

        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //TODO: Validate Fields data.
                //TODO: Check that account doesn't exist.

                if( ! siteName.getText().toString().isEmpty() ){

                    if( isNewAccount ) {
                        swiftLogin.addAccount(siteName.getText().toString(), userName.getText().toString(),
                                password.getText().toString(), email.getText().toString(),
                                siteURL.getText().toString(), siteLoginURL.getText().toString(), logo);
                        setResult(11);
                    } else {
                        swiftLogin.updateAccount(accountId, siteName.getText().toString(), userName.getText().toString(),
                                password.getText().toString(), email.getText().toString(),
                                siteURL.getText().toString(), siteLoginURL.getText().toString(), logo);
                        setResult(10);
                    }

                    finish();
                }
            }
        });

        ImageView genPWBtn = (ImageView) findViewById(R.id.PWgen);

        genPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog pwGenDialog = new Dialog(mContext, R.style.mydialogstyle);

                pwGenDialog.setTitle("Password Generator");
                pwGenDialog.setContentView(R.layout.generator_dialog);

                final EditText editGenPW = (EditText) pwGenDialog.findViewById(R.id.GenPW);
                SeekBar seekBar = (SeekBar) pwGenDialog.findViewById(R.id.seekBar);
                final CheckBox cbSymbols = (CheckBox) pwGenDialog.findViewById(R.id.checkBoxSymbols);
                final CheckBox cbDigits = (CheckBox) pwGenDialog.findViewById(R.id.checkBoxDigits);
                final CheckBox cbLetters = (CheckBox) pwGenDialog.findViewById(R.id.checkBoxLetters);
                Button confirmBtn = (Button) pwGenDialog.findViewById(R.id.confirmBtn);
                Button copyBtn = (Button) pwGenDialog.findViewById(R.id.copyBtn);

                cbSymbols.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        editGenPW.setText(RandomPasswordGenerator.generatePswd(seekBar.getMax(), cbLetters.isChecked(), cbDigits.isChecked(), cbSymbols.isChecked()));
                    }
                });

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        password.setText(editGenPW.getText());
                        pwGenDialog.dismiss();
                    }
                });

                copyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                     swiftLogin.clip(editGenPW.getText(), );
                        Toast.makeText(getBaseContext(), "Password has been copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });

                editGenPW.setText(RandomPasswordGenerator.generatePswd(8,true,true,true));
                pwGenDialog.show();
            }
        });
    }

}
