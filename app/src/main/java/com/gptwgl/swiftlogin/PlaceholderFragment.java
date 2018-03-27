package com.gptwgl.swiftlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment  implements HomeActivity.DataUpdateListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private List<Map<String, String>> accounts;
    private SwiftLogin swiftLogin;
    private CustomListViewAdapter homelvAdapter;
    private Activity parentActivity;
    private boolean actionModeOpen = false;
    private ActionMode actionMode;
    private String sessionsJSON;
    private ListView remoteSessionsLv;
    private DevicesListViewAdapter sessionsLvAdapter;
    private List<Map<String, String>> mDevices;
    private List<String> selectedAccountIds;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        swiftLogin = new SwiftLogin(getActivity());

        View rootView, rootView2;

        rootView = inflater.inflate(R.layout.logins_section, container, false);

        parentActivity = getActivity();
        accounts = swiftLogin.getAccounts();

        // Home List View.
        final ListView homelv = (ListView) rootView.findViewById(R.id.home_listview);

        homelvAdapter = new CustomListViewAdapter(getContext(), accounts);
        homelv.setAdapter(homelvAdapter);

        homelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ViewAccount.class);
                intent.putExtra("account_id", Integer.parseInt(((Map<String, String>) accounts.get(position)).get("id")));
                startActivity(intent);
            }
        });


        rootView2 = inflater.inflate(R.layout.sessions_section, container, false);

        remoteSessionsLv = (ListView) rootView2.findViewById(R.id.remote_sessions_list);

        mDevices = swiftLogin.getDevices();
        sessionsLvAdapter = new DevicesListViewAdapter(getContext(), mDevices );

        remoteSessionsLv.setAdapter(sessionsLvAdapter);

        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){

            // FAB
            final FloatingActionButton fab = (FloatingActionButton) parentActivity.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( actionModeOpen ){
                        SparseBooleanArray selectedIds = homelvAdapter.getSelectedIds();

                        short size = (short) selectedIds.size();
                        selectedAccountIds = new ArrayList<String>();

                        for (byte i = 0; i < size; i++) {
                            int accId = homelvAdapter.getAccountId(selectedIds.keyAt(i));
                            selectedAccountIds.add(String.valueOf(accId));
                        }

                        new IntentIntegrator(parentActivity)
                                .setCaptureActivity(ToolbarCaptureActivity.class)
                                .addExtra("accIds", (new Gson()).toJson(selectedIds) )
                                .initiateScan();

                        actionMode.finish();
                    } else {
                        ((HomeActivity) parentActivity).showDialog();
                    }
                }
            });

            // List View Accounts Selection.
            homelv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            // Capture ListView item click
            homelv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {

                    // Prints the count of selected Items in title
                    mode.setTitle(homelv.getCheckedItemCount() + " accounts selected");

                    // Toggle the state of item after every click on it
                    homelvAdapter.toggleSelection(position);
                }

                /**
                 * Called to report a user click on an action button.
                 * @return true if this callback handled the event,
                 *          false if the standard MenuItem invocation should continue.
                 */
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                /**
                 * Called when action mode is first created.
                 * The menu supplied will be used to generate action buttons for the action mode.
                 * @param mode ActionMode being created
                 * @param menu Menu used to populate action buttons
                 * @return true if the action mode should be created,
                 *          false if entering this mode should be aborted.
                 */
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    actionMode = mode;
                    homelvAdapter.initSparceBooleanArray();
                    actionModeOpen = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_flight_black_24dp, parentActivity.getTheme()));
                    }
//                        mode.getMenuInflater().inflate(R.menu.menu_main, menu);

                    return true;
                }

                /**
                 * Called when an action mode is about to be exited and destroyed.
                 */
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionModeOpen = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp, parentActivity.getTheme()));
                    }
                    //  mAdapter.removeSelection();
                }

                /**
                 * Called to refresh an action mode's action menu whenever it is invalidated.
                 * @return true if the menu or action mode was updated, false otherwise.
                 */
                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });

            return rootView;
        } else {
            return rootView2;
        }

    }

    public void updateList(){
        accounts = swiftLogin.getAccounts();
        homelvAdapter.setList(accounts);
        homelvAdapter.notifyDataSetChanged();
//        Toast.makeText(parentActivity, "New account added Successfully!", Toast.LENGTH_LONG).show();
    }

    public void updateSessionsList(){
        mDevices = swiftLogin.getDevices();
        sessionsLvAdapter.setList(mDevices);
        sessionsLvAdapter.notifyDataSetChanged();
//        Toast.makeText(parentActivity, "Sessions have been sent!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).registerDataUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) parentActivity).unregisterDataUpdateListener(this);
    }

    @Override
    public void onDataUpdate() {
        updateSessionsList();
        updateList();
    }

    public void sendSessions( String device_fid, String OS, String deviceName ){
        if( selectedAccountIds.size() != 0 ){
            swiftLogin.combineAndSendSessions( device_fid, selectedAccountIds, getActivity(), OS, deviceName  );
        }
        actionMode.finish();
    }
}
