package com.gptwgl.swiftlogin;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xwalk.core.XWalkCookieManager;

import java.util.Map;

public class ViewAccount extends AppCompatActivity {

    private int accountId;
    private Context mContext = this;
    private SwiftLogin swiftLogin;
    private TextView siteName;
    private XWalkCookieManager xWCM = new XWalkCookieManager();
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        swiftLogin = new SwiftLogin(mContext);

        accountId = getIntent().getExtras().getInt("account_id");
        session = swiftLogin.getSession(accountId);
        setViews();
    }

    public void setViews(){
        // Get Fields & Widgets.
        ImageView accLogo = (ImageView) findViewById(R.id.view_acc_logo);
        siteName = (TextView) findViewById(R.id.view_acc_site_name);
        TextView username = (TextView) findViewById(R.id.view_acc_username);
        TextView email = (TextView) findViewById(R.id.view_acc_email);
        Button sessionBtn = (Button) findViewById(R.id.session_button);
        TextView url = (TextView) findViewById(R.id.view_acc_site_url);

        final TextView password = (TextView) findViewById(R.id.password);
        final ImageView visibility = (ImageView) findViewById(R.id.visibilityIV);

        // Retrieve account details and set Fields & Widgets.
        Map<String, String> account = swiftLogin.getAccount(accountId);
        siteName.setText(account.get("site_name"));
        username.setText(account.get("username"));
        email.setText(account.get("email"));
        url.setText(account.get("site_url"));
        int resId = mContext.getResources().getIdentifier(account.get("logo"), "drawable", mContext.getPackageName());
        accLogo.setImageResource(resId);

        setTitle(account.get("site_name"));

        password.setInputType( InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        password.setText(account.get("password"));

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Password has been copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType( InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    password.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }

            }
        });



        if( session != null ){
            if( session.cookies.size() == 0 ){
                sessionBtn.setText("Create Session");
            } else {
                sessionBtn.setText("Delete Session");
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClickEdit(MenuItem item){
        Intent intent = new Intent(mContext, EditAccount.class);
        intent.putExtra("new_account", false);
        intent.putExtra("account_id", accountId);
        startActivityForResult(intent, 2);
    }

    public void onClickDelete(MenuItem item){
        swiftLogin.removeAccount(siteName.getText().toString());
        Toast.makeText(mContext, "Account Deleted Successfully!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
    }

    public void onClickSessionBtn(View v){
        Button sessionBtn = (Button)v;


        if( sessionBtn.getText().toString().equals("Create Session") ){
            Intent intent = new Intent(mContext, AccountLogin.class);
            intent.putExtra("account_id", accountId);
            startActivityForResult(intent, 5);
        } else {
            swiftLogin.clearLocalSession(accountId);
            sessionBtn.setText("Create Session");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 2 && resultCode == 10 ){
            setViews();
            Toast.makeText(mContext, "Account has been updated.", Toast.LENGTH_LONG).show();
        }

        if( requestCode == 5 && resultCode == 16 ){
            Toast.makeText(mContext, "Your session has been saved.", Toast.LENGTH_LONG).show();
        }
    }

}
