package com.gptwgl.swiftlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class CustomDialogListViewAdapter extends BaseAdapter {
    private final List<Map<String, String>> mData;
    private Context mContext;
    private ViewHolder viewHolder;

    public CustomDialogListViewAdapter(Context context, List<Map<String, String>> mapList) {
        mData = mapList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    private static class ViewHolder {
        ImageView logo;
        TextView siteName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dialog_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.logo = (ImageView) convertView.findViewById(R.id.dialogImageView);
            viewHolder.siteName = (TextView) convertView.findViewById(R.id.dialogTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map<String, String> item = getItem(position);
        int resId = mContext.getResources().getIdentifier(item.get("logo"), "drawable", mContext.getPackageName());




        viewHolder.siteName.setText(item.get("site_name"));
//        viewHolder.logo.setImageResource(resId);
        Picasso.with(mContext).load(resId).into(viewHolder.logo);

        return convertView;
    }
}
