package com.example.a_final_201737079;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class listAdapter extends BaseAdapter {

    private ArrayList<listData> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public listData getItem(int position) {

        return items.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.address, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        TextView tvPoint = (TextView) convertView.findViewById(R.id.tvPoint);
        TextView tvPoint2 = (TextView) convertView.findViewById(R.id.tvPoint2);


        listData item = getItem(position);

        tvTitle.setText(item.getTvTitle());
        tvAddress.setText(item.getTvAddress());
        tvPoint.setText(item.getLat());
        tvPoint2.setText(item.getLon());


        return convertView;
    }

    public void addItem(String name, String address, double point, double point2) {
        listData item = new listData();

        item.setTvTitle(name);
        item.setTvAddress(address);
        item.setLat(Double.toString(point));
        item.setLon(Double.toString(point2));

        items.add(item);
    }

    public void clearItem(){
        items.clear();
    }
}