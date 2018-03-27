package com.gptwgl.swiftlogin;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CustomListViewAdapter extends BaseAdapter {
    private List<Map<String, String>> mData;
    private Context mContext;
    private SparseBooleanArray mSelectedItemsIds;

    public CustomListViewAdapter(Context context, List<Map<String, String>> mapList) {
        mData = mapList;
        mContext = context;
    }

    public void initSparceBooleanArray(){
        mSelectedItemsIds = new SparseBooleanArray();
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
        return 0;
    }

    public int getAccountId(int position){
        return Integer.parseInt(mData.get(position).get("id"));
    }

    public String getItemSiteName(int position) {
        return mData.get(position).get("site_name");
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private static class ViewHolder2 {
        ImageView logo;
        TextView siteName;
        TextView email;
    }

    public void setList(List<Map<String, String>> newlist){
        mData = newlist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder2 viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_row, parent, false);

            viewHolder = new ViewHolder2();
            viewHolder.logo = (ImageView) convertView.findViewById(R.id.homelv_logo);
            viewHolder.siteName = (TextView) convertView.findViewById(R.id.homelv_site_name);
            viewHolder.email = (TextView) convertView.findViewById(R.id.homelv_email);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder2) convertView.getTag();
        }

        Map<String, String> item = getItem(position);


        int resId = mContext.getResources().getIdentifier(item.get("logo"), "drawable", mContext.getPackageName());

        viewHolder.logo.setImageResource(resId);
        viewHolder.siteName.setText(item.get("site_name"));
        viewHolder.email.setText(item.get("email"));
//        Picasso.with(mContext).load(resId).into(viewHolder.logo);

        return convertView;
    }
}
