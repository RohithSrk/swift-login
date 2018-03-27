package com.gptwgl.swiftlogin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import org.xwalk.core.XWalkCookieManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext = this;
    private SwiftLogin swiftLogin;
    private XWalkCookieManager xWCM = new XWalkCookieManager();
    private Activity activity = this;
    SharedPreferences sharedpreferences;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Dialog dialog;
    private List<DataUpdateListener> mListeners;

    private Snackbar skBar;

    private Subscription networkConnectivitySubscription;
    private Subscription internetConnectivitySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mListeners = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        swiftLogin = new SwiftLogin(mContext);

        skBar = Snackbar.make(activity.findViewById(R.id.content_home), "Network is not available", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null);

        sharedpreferences = getSharedPreferences("swiftLoginPrefs",
                Context.MODE_PRIVATE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.tabs_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        // Add Account Dialog.

        final List<Map<String, String>> templates = swiftLogin.getTemplates();
        dialog = new Dialog(mContext, R.style.mydialogstyle);

        dialog.setContentView(R.layout.custom_dialog_list);
        final ListView lv = (ListView) dialog.findViewById(R.id.dialog_list_view);
        templates.add(new HashMap<String, String>(){{
            put("id", "other");
            put("site_name", "Other");
            put("logo", "other");
        }});

        lv.setAdapter(new CustomDialogListViewAdapter(mContext, templates));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EditAccount.class);
                intent.putExtra("template_id", ((Map<String, String>) templates.get(position)).get("id"));
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });


        // Drawer and Navigation.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }catch (NullPointerException e){

        }
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar.setNavigationIcon(null);
    }

    public void lockData(MenuItem item){

        Intent intent;
        if( sharedpreferences.contains("fingerPrintAvailable") ){
            intent = new Intent(getBaseContext(), FingerPrintLockScreen.class);
        } else {
            intent = new Intent(getBaseContext(), MasterPasswordLockScreen.class);
        }
        startActivity(intent);
        finish();
    }

    public void showDialog(){
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_item) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PlaceholderFragment section1;

        if( requestCode == 1 && resultCode == 11 ){
            dataUpdated();
        }

        String toast;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {

            if(result.getContents() == null) {
                toast = "Cancelled from fragment";
            } else {
                toast = "Scanned from fragment: " + result.getContents();

                String[] resultData = result.getContents().split(":");

                mListeners.get(0).sendSessions( resultData[0], resultData[1], resultData[2] );
            }

            displayToast(toast);
        }

    }

    private void displayToast(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
    }

    public void onClickClearCookies(MenuItem item){
        for (int i =0; i < 2; i++){
            xWCM.removeAllCookie();
            xWCM.flushCookieStore();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Logins";
                case 1:
                    return "Sessions";
            }
            return null;
        }
    }

    public interface DataUpdateListener {
        void onDataUpdate();
        void sendSessions( String device_fid, String OS, String deviceName );
    }

    public synchronized void registerDataUpdateListener(DataUpdateListener listener) {
        mListeners.add(listener);
    }

    public synchronized void dataUpdated() {
        for (DataUpdateListener listener : mListeners) {
            listener.onDataUpdate();
        }
    }

    public synchronized void unregisterDataUpdateListener(DataUpdateListener listener) {
        mListeners.remove(listener);
    }


    @Override
    protected void onResume() {
        super.onResume();

        networkConnectivitySubscription =
                ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Connectivity>() {
                            @Override public void call(final Connectivity connectivity) {
                                Log.d("TESTIOO", connectivity.toString());
                                final NetworkInfo.State state = connectivity.getState();
                                final String name = connectivity.getTypeName();

                                if(state.toString().equals( "DISCONNECTED" )){
                                    skBar.show();
                                } else {
                                    swiftLogin.ws = null;
                                    SwiftLogin.connect();
                                    skBar.dismiss();
                                }

                                Log.d("TESTIO",String.format("state: %s, typeName: %s", state, name));
                            }
                        });
    }
}