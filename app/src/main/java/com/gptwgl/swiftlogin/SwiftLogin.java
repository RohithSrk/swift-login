package com.gptwgl.swiftlogin;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import static android.content.Context.CLIPBOARD_SERVICE;

class SlJson {
    public String type;
}

class DeviceMeta {
    public String deviceName;
    public String OS;
    public String from_id;
}

class SlDevice {
    public String type;
    public String device_id;
}

public class SwiftLogin {

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;
    private static final String TAG = "DataAdapter";

    public static WebSocket ws = null;
    public static String wsId;

    SwiftLogin(Context context) {
        mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
        this.createDatabase();
        connect();
    }

    public static void connect(){

        if(ws == null){

            try{
                ws = new WebSocketFactory().createSocket("ws://139.59.35.59:5959/slserver", 5000);
                ws.connectAsynchronously();

                ws.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) throws Exception {
                        Log.i("Received Message", "");

                        SlJson json = new Gson().fromJson(message, SlJson.class);

                        if(json.type.equals( "new_connection" )){
                            SlDevice device = new Gson().fromJson(message, SlDevice.class);
                            wsId = device.device_id;
                        }
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

                        ws.recreate().connect();
                    }


                });


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private SwiftLogin createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    private SwiftLogin open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }


    private void close()
    {
        mDbHelper.close();
    }

    public List<Map<String, String>> getTemplates(){
        try
        {
            this.open();

            List<Map<String, String>> templates = new ArrayList();

            String sql = "SELECT * FROM templates";
            Cursor cursor = mDb.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Map<String, String> template = new HashMap<String, String>();

                    template.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    template.put("site_name", cursor.getString(cursor.getColumnIndex("site_name")));
                    template.put("site_url", cursor.getString(cursor.getColumnIndex("site_url")));
                    template.put("site_login_url", cursor.getString(cursor.getColumnIndex("site_login_url")));
                    template.put("logo", cursor.getString(cursor.getColumnIndex("logo")));
                    templates.add(template);

                    cursor.moveToNext();
                }
            }

            this.close();

            return templates;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Map<String, String> getTemplate(int template_id){
        try
        {
            this.open();

            Map<String, String> template = new HashMap();

            String sql = "SELECT * FROM templates where id=" + template_id;
            Cursor cursor = mDb.rawQuery(sql, null);

            if (cursor .moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    template.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    template.put("site_name", cursor.getString(cursor.getColumnIndex("site_name")));
                    template.put("site_url", cursor.getString(cursor.getColumnIndex("site_url")));
                    template.put("site_login_url", cursor.getString(cursor.getColumnIndex("site_login_url")));
                    template.put("logo", cursor.getString(cursor.getColumnIndex("logo")));

                    cursor.moveToNext();
                }
            }

            this.close();

            return template;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addAccount(String siteName, String userName, String password, String email,
                           String url, String loginUrl, String logo){
        try
        {
            this.open();

            String sql = "INSERT INTO accounts (site_name, site_url, site_login_url, username, email, password, logo) VALUES " +
                    "('" + siteName + "','" + url + "','" + loginUrl + "','" + userName + "','" +
                    email + "','" + password + "','" + logo +"')";
            mDb.execSQL(sql);

            this.close();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void removeAccount(String siteName){
        try
        {
            this.open();

            String sql = "DELETE FROM accounts where site_name=" + "'"+ siteName + "'";
            mDb.execSQL(sql);

            this.close();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public List<Map<String,String>> getAccounts() {
        try
        {
            this.open();

            List<Map<String, String>> accounts = new ArrayList();

            String sql = "SELECT id, site_name, email, logo FROM accounts";
            Cursor cursor = mDb.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Map<String, String> account = new HashMap<String, String>();

                    account.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    account.put("site_name", cursor.getString(cursor.getColumnIndex("site_name")));
                    account.put("email", cursor.getString(cursor.getColumnIndex("email")));
                    account.put("logo", cursor.getString(cursor.getColumnIndex("logo")));
                    accounts.add(account);

                    cursor.moveToNext();
                }
            }

            this.close();

            return accounts;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Map<String, String> getAccount(int account_id){
        try
        {
            this.open();

            Map<String, String> account = new HashMap();

            String sql = "SELECT * FROM accounts where id=" + account_id;
            Cursor cursor = mDb.rawQuery(sql, null);

            if (cursor .moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    account.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    account.put("site_name", cursor.getString(cursor.getColumnIndex("site_name")));
                    account.put("site_url", cursor.getString(cursor.getColumnIndex("site_url")));
                    account.put("site_login_url", cursor.getString(cursor.getColumnIndex("site_login_url")));
                    account.put("username", cursor.getString(cursor.getColumnIndex("username")));
                    account.put("password", cursor.getString(cursor.getColumnIndex("password")));
                    account.put("email", cursor.getString(cursor.getColumnIndex("email")));
                    account.put("logo", cursor.getString(cursor.getColumnIndex("logo")));

                    cursor.moveToNext();
                }
            }

            this.close();

            return account;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateAccount(int accID, String siteName, String userName, String password, String email,
                           String url, String loginUrl, String logo){
        try
        {
            this.open();

            String sql = "UPDATE accounts SET site_name='"+ siteName +"', site_url='"+ url + "', site_login_url='"+ loginUrl
                    + "', username='"+ userName +"', email='"+ email +"', password='"+ password +"', logo='"+ logo +"' WHERE id=" + accID;

            mDb.execSQL(sql);

            this.close();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateSession( int accID, String gsonSession ){
        String base64Session = Base64.encodeToString(gsonSession.getBytes(), Base64.DEFAULT);

        try
        {
            this.open();

            String sql = "UPDATE accounts SET session='"+ base64Session +"' WHERE id=" + accID;

            mDb.execSQL(sql);

            this.close();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Session getSession( int accID ){
        try
        {
            this.open();

            String sql = "SELECT session FROM accounts where id=" + accID;
            Cursor cursor = mDb.rawQuery(sql, null);
            String gsonSession = "";
            if (cursor .moveToFirst()) {
                String ss = cursor.getString(cursor.getColumnIndex("session"));

                if(ss != null){
                    gsonSession = new String(Base64.decode(cursor.getString(cursor.getColumnIndex("session")), Base64.DEFAULT));
                } else {
                    return null;
                }

            }
            Gson gson = new Gson();
            Session session = gson.fromJson(gsonSession, Session.class);

            this.close();

            return session;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }


    public void sendSessions(final String deviceId, String sessions, final Activity activity ){

        Snackbar.make(activity.findViewById(R.id.content_home), "Your sessions have been sent.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

        if( ws != null ) {
            Map<String, String> sess = new HashMap();
            sess.put("device_id", deviceId);
            sess.put("type", "send_sessions");
            sess.put("sessions", sessions);
            sess.put("from_id", wsId);

            ws.sendText((new Gson().toJson(sess)));
        }

    }

    public void removeRemoteSessions( final String deviceId, String sessions ){

        if( ws != null ) {
            Map<String, String> sess = new HashMap();
            sess.put("device_id", deviceId);
            sess.put("type", "remove_sessions");
            sess.put("sessions", sessions);
            sess.put("from_id", wsId);

            ws.sendText((new Gson().toJson(sess)));
        }
    }

    public void clearLocalSession(int accountId){

        try
        {
            this.open();

            String sql = "UPDATE accounts SET session='' WHERE id=" + accountId;

            mDb.execSQL(sql);

            this.close();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public long addDevice( String deviceFid, String OS, String name ){
        try
        {
            this.open();

            ContentValues values = new ContentValues();

            values.put( "device_fid", deviceFid );
            values.put( "operating_system", OS );
            values.put( "device_name", name );

            long id = mDb.insert("devices", null, values);
            this.close();

            return id;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public long getDeviceId( String deviceFid ){
        try
        {
            this.open();

            String sql = "SELECT * FROM devices where device_fid='" + deviceFid + "'";
            Cursor cursor = mDb.rawQuery(sql, null);
            long deviceId = -1;

            if( cursor.getCount() != 0 ){
                if (cursor.moveToFirst()) {
                    deviceId = cursor.getLong(cursor.getColumnIndex("device_fid"));
                }
            }

            this.close();

            return deviceId;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public List<Map<String,String>> getDevices(){
        try
        {
            this.open();

            List<Map<String, String>> devices = new ArrayList();

            String sql = "SELECT id, device_name, operating_system FROM devices";
            Cursor cursor = mDb.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Map<String, String> device = new HashMap<String, String>();

                    String deviceId = cursor.getString(cursor.getColumnIndex("id"));
                    device.put("id", deviceId);
                    device.put("device_name", cursor.getString(cursor.getColumnIndex("device_name")));
                    device.put("operating_system", cursor.getString(cursor.getColumnIndex("operating_system")));
                    device.put("session_count", String.valueOf( getSessionCount( Integer.parseInt( deviceId ))));
                    devices.add(device);

                    cursor.moveToNext();
                }
            }

            this.close();

            return devices;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public int getSessionCount( int deviceId ){
        return 10;
    }

    public void addRemoteSession( long deviceId, int accId ){
        try
        {
            this.open();

            String sql = "SELECT * FROM remote_sessions where device_id=" + deviceId + " AND account_id=" + accId;
            Cursor cursor = mDb.rawQuery(sql, null);

            if( cursor.getCount() == 0 ){

                ContentValues values = new ContentValues();

                values.put( "device_id", deviceId );
                values.put( "account_id", accId );

                mDb.insert("remote_sessions", null, values);
            }

            this.close();

        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }


    public boolean removeDeviceAndRemoteSessions( int deviceId ){

        // TODO:// Delete only on confirmation
        this.open();

        String sql = "select account_id, device_fid from remote_sessions left join devices on remote_sessions.device_id = devices.id where device_id=" + deviceId;
        String device_fid = "";
        Cursor cursor = mDb.rawQuery(sql, null);
        List<Session> sessionsToRemove = new ArrayList<Session>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                device_fid = cursor.getString(cursor.getColumnIndex("device_fid"));
                int accId = cursor.getInt(cursor.getColumnIndex("account_id"));
                sessionsToRemove.add(getSession(accId));
                cursor.moveToNext();
            }
        }



        String sessionsJSON = (new Gson()).toJson(sessionsToRemove);
        removeRemoteSessions( device_fid, sessionsJSON );

        this.close();


        return true;
    }

    public void deleteDevice( int deviceId ){
        this.open();
        mDb.delete("devices", "id=" + deviceId, null);
        mDb.delete("remote_sessions", "device_id=" + deviceId, null);
        this.close();
    }

    public void combineAndSendSessions(final String device_fid, final List<String> accIds, final Activity activity, String OS, String deviceName ){
        List<Session> sessions = new ArrayList<Session>();

        for (String accId : accIds){
            sessions.add(getSession(Integer.parseInt(accId)));
        }

        String sessionsJSON = (new Gson()).toJson(sessions);
        sendSessions( device_fid, sessionsJSON, activity );

        long deviceId = getDeviceId( device_fid );

        if( deviceId < 0 ){

            long newDeviceId = addDevice( device_fid, OS, deviceName );

            for ( String accId : accIds ){
                addRemoteSession( newDeviceId, Integer.parseInt(accId) );
            }
            ((HomeActivity) activity).dataUpdated();

        } else {
            for (String accId : accIds){
                addRemoteSession( deviceId, Integer.parseInt(accId) );
                ((HomeActivity) activity).dataUpdated();
            }
        }

    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void clip(String str, AppCompatActivity activity){
        //TODO:// Add Clipboard Functionality
        ClipboardManager myClipboard;

        myClipboard = (ClipboardManager)activity.getSystemService(CLIPBOARD_SERVICE);

        Toast.makeText(activity, "Copied!", Toast.LENGTH_SHORT).show();
    }
}
