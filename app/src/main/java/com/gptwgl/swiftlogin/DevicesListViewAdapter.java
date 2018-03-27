package com.gptwgl.swiftlogin;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DevicesListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Map<String, String>> mData;
    private SwiftLogin swiftLogin;

    DevicesListViewAdapter(Context context, List<Map<String, String>> devices){
        mContext = context;
        mData = devices;
        swiftLogin = new SwiftLogin(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Map<String, String> getItem(int position) {
        return mData.get(position);
    }

    public int getDeviceId(int position){
        return Integer.parseInt(mData.get(position).get("id"));
    }

    public void setList(List<Map<String, String>> newlist){
        mData = newlist;
    }

    private static class ViewHolder2 {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceSub;
        ImageView deleteIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder2 viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_row, parent, false);

            viewHolder = new ViewHolder2();
            viewHolder.deviceIcon = (ImageView) convertView.findViewById(R.id.device_icon);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.deviceSub = (TextView) convertView.findViewById(R.id.device_sub);
            viewHolder.deleteIcon = (ImageView) convertView.findViewById(R.id.delete_device_sessions);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder2) convertView.getTag();
        }

        Map<String, String> item = getItem(position);

        String os = item.get("operating_system");

        if( os == null ) os = "other_os";

        int resId = mContext.getResources().getIdentifier(os.toLowerCase(), "drawable", mContext.getPackageName());

        viewHolder.deviceIcon.setImageResource(resId);
        viewHolder.deleteIcon.setTag(R.string.delete_icon_account_id, item.get("id"));
        viewHolder.deleteIcon.setTag(R.string.delete_icon_item_position, position);
        viewHolder.deleteIcon.setTag(item.get("id"));
        viewHolder.deviceName.setText(item.get("device_name"));
        viewHolder.deviceSub.setText(item.get("session_count") + " active sessions");

        viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swiftLogin.removeDeviceAndRemoteSessions(Integer.valueOf(view.getTag(R.string.delete_icon_account_id).toString()));
                swiftLogin.deleteDevice(Integer.valueOf(view.getTag(R.string.delete_icon_account_id).toString()));
                mData.remove((int) view.getTag(R.string.delete_icon_item_position));
                notifyDataSetChanged();
                Snackbar.make(view, "Remote Sessions Cleared Successfully!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return convertView;
    }
}
