package com.arduino.blue.theglenn.arduinoblue;

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
    private ArrayList<BluetoothDevice> items;
    private LayoutInflater inflater;

    public BlueAdapter(Context context, int layoutResourceId, ArrayList<BluetoothDevice> items) {
        super(context, layoutResourceId, items);
        this.items = items;
        this.layoutResourceId = layoutResourceId;
        this.inflater = LayoutInflater.from(getContext());
    }

    public BlueAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.items = new ArrayList<BluetoothDevice>();
        this.layoutResourceId = layoutResourceId;
        this.inflater = LayoutInflater.from(getContext());
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
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolderDevice holder;
        if (view != null) {
            holder = (ViewHolderDevice) view.getTag();
        } else {

            view = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolderDevice();
            holder.textView1 = (TextView) view.findViewById(android.R.id.text1);
            holder.textView2 = (TextView) view.findViewById(android.R.id.text2);

            view.setTag(holder);
        }

        BluetoothDevice bd = getItem(position);

        holder.textView1.setText(bd.getName());
        holder.textView2.setText(bd.getAddress());

        return view;
    }

    static class ViewHolderDevice {
        TextView textView1;
        TextView textView2;
    }
}
