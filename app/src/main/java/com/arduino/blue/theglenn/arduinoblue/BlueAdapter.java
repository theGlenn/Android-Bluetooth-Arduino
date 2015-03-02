package com.arduino.blue.theglenn.arduinoblue;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by theGlenn on 15/10/2014.
 */
public class BlueAdapter extends ArrayAdapter<BluetoothDevice> {

    private final int layoutResourceId;
    ArrayList<BluetoothDevice> items;

    public BlueAdapter(Context context, int layoutResourceId, ArrayList<BluetoothDevice> items) {
        super(context, layoutResourceId, items);
        this.items = items;
        this.layoutResourceId = layoutResourceId;
    }

    public BlueAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.items = new ArrayList<BluetoothDevice>();
        this.layoutResourceId = layoutResourceId;
    }

    public ArrayList<BluetoothDevice> getItems() {
        return items;
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return items.get(position);
    }

    @Override
    public void add(BluetoothDevice newDailyData) {
        items.add(newDailyData);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutResourceId, null);
        }

        BluetoothDevice bd = getItem(position);

        if (bd != null) {

            TextView t1 = (TextView) v.findViewById(android.R.id.text1);
            TextView t2 = (TextView) v.findViewById(android.R.id.text2);

            t1.setText(bd.getName());
            t1.setText(bd.getAddress());
        }

        return v;
    }
}
